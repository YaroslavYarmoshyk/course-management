package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public Course findByCode(final Long code) {
        return courseRepository.findByCode(code)
                .map(entity -> mapper.map(entity, Course.class))
                .orElseThrow(() -> new SystemException("Course with code " + code + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public Course save(final Course course) {
        final CourseEntity savedCourseEntity = courseRepository.save(mapper.map(course, CourseEntity.class));
        return mapper.map(savedCourseEntity, Course.class);
    }

    @Override
    public CourseAssignmentResponseDto assignInstructor(final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        final User user = userService.findById(courseAssignmentRequestDto.userId());
        validateInstructorAssignment(user);
        final Course course = findByCode(courseAssignmentRequestDto.courseCode());
        course.getUsers().add(user);
        final Course savedCourse = save(course);
        final Map<Role, Set<UserCourseDto>> usersByRole = getGroupedUsersByRole(savedCourse);
        return new CourseAssignmentResponseDto(
                savedCourse.getCode(),
                savedCourse.getTitle(),
                usersByRole.get(Role.INSTRUCTOR),
                usersByRole.get(Role.STUDENT)
        );
    }

    private void validateInstructorAssignment(final User potentialInstructor) {
        potentialInstructor.getRoles().stream()
                .filter(role -> role.equals(Role.INSTRUCTOR))
                .findAny()
                .orElseThrow(() -> new SystemException("Cannot assign user with id " + potentialInstructor.getId() +
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
