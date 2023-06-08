package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
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
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public Course getByCode(final Long code) {
        return courseRepository.findByCode(code)
                .map(entity -> mapper.map(entity, Course.class))
                .orElseThrow(() -> new SystemException("Course with code " + code + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public Course save(final Course course) {
        final CourseEntity courseEntity = mapper.map(course, CourseEntity.class);
        final CourseEntity savedCourse = courseRepository.save(courseEntity);
        return mapper.map(savedCourse, Course.class);
    }

    @Override
    @Transactional
    public CourseAssignmentResponseDto assignInstructor(final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        final User user = userService.getById(courseAssignmentRequestDto.userId());
        validateInstructorAssignment(user);
        final Course course = getByCode(courseAssignmentRequestDto.courseCode());
        course.getUsers().add(user);
        final Course savedCourse = save(course);
        final Map<Role, Set<UserCourseDto>> usersByRole = getGroupedUsersByRole(savedCourse);
        return new CourseAssignmentResponseDto(
                savedCourse.getCode(),
                savedCourse.getTitle(),
                usersByRole.getOrDefault(Role.INSTRUCTOR, Set.of()),
                usersByRole.getOrDefault(Role.STUDENT, Set.of())
        );
    }

    @Override
    public Set<Course> getAllByCodes(final Collection<Long> codes) {
        return courseRepository.findAllByCodeIn(codes).stream()
                .map(courseEntity -> mapper.map(courseEntity, Course.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Course> getAllActiveByUserId(final Long userId) {
        final Set<CourseEntity> courseEntities = courseRepository.findByUsersUserCourseIdUserEntityId(userId);
        return courseEntities.stream()
                .filter(courseEntity -> activeByUserId(courseEntity, userId))
                .map(courseEntity -> mapper.map(courseEntity, Course.class))
                .collect(Collectors.toSet());
    }

    private static boolean activeByUserId(final CourseEntity courseEntity, final Long userId) {
        return Optional.ofNullable(courseEntity.getUsers())
                .stream()
                .flatMap(Collection::stream)
                .filter(userCourseEntity -> Objects.equals(userCourseEntity.getUserEntity().getId(), userId))
                .allMatch(UserCourseEntity::isActive);
    }

    @Override
    public Set<CourseDto> getAllByUserId(final Long userId) {
        final Set<CourseEntity> courseEntities = courseRepository.findByUsersUserCourseIdUserEntityId(userId);
        return courseEntities.stream()
                .map(courseEntity -> new CourseDto(courseEntity, activeByUserId(courseEntity, userId)))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Course> addStudentToCourses(final User student, final Collection<Course> courses) {
        for (final Course course : courses) {
            final Set<User> users = course.getUsers();
            users.add(student);
        }
        final Set<CourseEntity> courseEntities = courses.stream()
                .map(course -> mapper.map(course, CourseEntity.class))
                .collect(Collectors.toSet());
        final List<CourseEntity> savedCourseEntities = courseRepository.saveAll(courseEntities);
        return savedCourseEntities.stream()
                .map(courseEntity -> mapper.map(courseEntity, Course.class))
                .collect(Collectors.toSet());
    }

    private void validateInstructorAssignment(final User potentialInstructor) {
        potentialInstructor.getRoles().stream()
                .filter(role -> role.equals(Role.INSTRUCTOR))
                .findAny()
                .orElseThrow(() -> new SystemException("Cannot assign user with userId " + potentialInstructor.getId() +
                        " to the course, the user is not an instructor", SystemErrorCode.BAD_REQUEST));
    }

    private Map<Role, Set<UserCourseDto>> getGroupedUsersByRole(final Course course) {
        final Set<User> users = course.getUsers();
        return users.stream()
                .flatMap(user -> user.getRoles().stream().map(
                        role -> new AbstractMap.SimpleEntry<>(role, new UserCourseDto(user)))
                )
                .collect(
                        Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                                Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toSet()))
                );
    }
}
