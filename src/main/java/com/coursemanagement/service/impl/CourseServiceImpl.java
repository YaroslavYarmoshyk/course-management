package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
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
        return getAllUserCoursesByUserId(userId).stream()
                .map(CourseDto::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UserCourse> getAllUserCoursesByUserId(final Long userId) {
        final List<UserCourseEntity> userCourseEntities = userCourseRepository.findByUserEntityId(userId);
        return userCourseEntities.stream()
                .map(userCourseEntity -> mapper.map(userCourseEntity, UserCourse.class))
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
        final Map<Role, Set<UserDto>> usersByRole = getGroupedUsersByRole(course);
        return new CourseAssignmentResponseDto(
                course.getCode(),
                course.getSubject(),
                usersByRole.getOrDefault(Role.INSTRUCTOR, Set.of()),
                usersByRole.getOrDefault(Role.STUDENT, Set.of())
        );
    }

    @Override
    public void addUserToCourses(final User user, final Collection<Long> courseCodes) {
        final Set<CourseEntity> courseEntities = courseRepository.findAllByCodeIn(courseCodes);
        final UserEntity userEntity = mapper.map(user, UserEntity.class);
        for (final CourseEntity courseEntity : courseEntities) {
            final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
            userCourseEntities.add(new UserCourseEntity(userEntity, courseEntity));
            userCourseEntities.forEach(userCourseEntity -> userCourseEntity.setStatus(UserCourseStatus.STARTED));
        }
        courseRepository.saveAll(courseEntities);
    }

    private void validateInstructorAssignment(final User potentialInstructor) {
        potentialInstructor.getRoles().stream()
                .filter(role -> role.equals(Role.INSTRUCTOR))
                .findAny()
                .orElseThrow(() -> new SystemException("Cannot assign user to the course, the user is not an instructor", SystemErrorCode.BAD_REQUEST));
    }

    private Map<Role, Set<UserDto>> getGroupedUsersByRole(final Course course) {
        return course.getUsers().stream()
                .flatMap(user -> user.getRoles().stream().map(
                        role -> new AbstractMap.SimpleEntry<>(role, new UserDto(user)))
                )
                .collect(
                        Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                                Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toSet()))
                );
    }
}
