package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.AdminService;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final CourseService courseService;

    @Override
    public User assignRoleToUser(final RoleAssignmentDto roleAssignmentDto) {
        final User user = userService.getUserById(roleAssignmentDto.userId());
        user.getRoles().addAll(roleAssignmentDto.roles());
        return userService.save(user);
    }

    @Override
    @Transactional
    public CourseAssignmentResponseDto assignInstructorToCourse(final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        final Long userId = courseAssignmentRequestDto.userId();
        final Long courseCode = courseAssignmentRequestDto.courseCode();
        final User potentialInstructor = userService.getUserById(userId);
        validateInstructorAssignment(potentialInstructor);

        courseService.addUserToCourses(potentialInstructor, Set.of(courseCode));

        final Course course = courseService.getCourseByCode(courseCode);
        final Map<Role, Set<UserDto>> usersByRole = getGroupedUsersByRole(course);
        return new CourseAssignmentResponseDto(
                course.getCode(),
                course.getSubject(),
                usersByRole.getOrDefault(Role.INSTRUCTOR, Set.of()),
                usersByRole.getOrDefault(Role.STUDENT, Set.of())
        );
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
