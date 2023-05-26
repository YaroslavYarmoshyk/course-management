package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AdminAccessLevel
@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final CourseService courseService;

    @PostMapping(value = "/assign-role")
    public UserDto assignRole(@RequestBody RoleAssignmentDto roleAssignmentDto) {
        return userService.assignRole(roleAssignmentDto);
    }

    @PostMapping(value = "/assign-instructor-to-course")
    public CourseAssignmentResponseDto assignInstructorToCourse(@RequestBody CourseAssignmentRequestDto courseAssignmentRequestDto) {
        return courseService.assignInstructorToCourse(courseAssignmentRequestDto);
    }
}
