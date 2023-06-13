package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserCourseService userCourseService;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public Course getByCode(final Long code) {
        return courseRepository.findByCode(code)
                .map(entity -> mapper.map(entity, Course.class))
                .orElseThrow(() -> new SystemException("Course with code " + code + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public Set<CourseDto> getAllByUserId(final Long userId) {
        return userCourseService.getAllByUserId(userId).stream()
                .map(CourseDto::new)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public CourseAssignmentResponseDto assignInstructor(final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        final Long userId = courseAssignmentRequestDto.userId();
        final Long courseCode = courseAssignmentRequestDto.courseCode();
        final User potentialInstructor = userService.getById(userId);
        validateInstructorAssignment(potentialInstructor);

        addUserToCourses(potentialInstructor, Set.of(courseCode));

        final Course course = getByCode(courseCode);
        final Map<Role, Set<UserCourseDto>> usersByRole = getGroupedUsersByRole(course);
        return new CourseAssignmentResponseDto(
                course.getCode(),
                course.getTitle(),
                usersByRole.getOrDefault(Role.INSTRUCTOR, Set.of()),
                usersByRole.getOrDefault(Role.STUDENT, Set.of())
        );
    }

    @Override
    public void addUserToCourses(final User user, final Collection<Long> courseCodes) {
        final Set<CourseEntity> courseEntities = courseRepository.findAllByCodeIn(courseCodes);
        for (final CourseEntity courseEntity : courseEntities) {
            final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
            userCourseEntities.add(new UserCourseEntity(user.getId(), courseEntity.getCode()));
            userCourseEntities.forEach(userCourseEntity -> userCourseEntity.setStatus(UserCourseStatus.STARTED));
        }
        courseRepository.saveAll(courseEntities);
    }

    private void validateInstructorAssignment(final User potentialInstructor) {
        potentialInstructor.getRoles().stream()
                .filter(role -> role.equals(Role.INSTRUCTOR))
                .findAny()
                .orElseThrow(() -> new SystemException("Cannot assign user with userId " + potentialInstructor.getId() +
                        " to the course, the user is not an instructor", SystemErrorCode.BAD_REQUEST));
    }

    private Map<Role, Set<UserCourseDto>> getGroupedUsersByRole(final Course course) {
        return course.getUsers().stream()
                .flatMap(user -> user.getRoles().stream().map(
                        role -> new AbstractMap.SimpleEntry<>(role, new UserCourseDto(user)))
                )
                .collect(
                        Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                                Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toSet()))
                );
    }
}
