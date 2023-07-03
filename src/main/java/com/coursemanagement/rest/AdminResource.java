package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AdminAccessLevel
@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminResource {
    private final AdminService adminService;

    @PostMapping(value = "/assign-role")
    public User assignRoleToUser(@RequestBody RoleAssignmentDto roleAssignmentDto) {
        return adminService.assignRoleToUser(roleAssignmentDto);
    }

    @PostMapping(value = "/assign-instructor")
    public CourseAssignmentResponseDto assignInstructor(@RequestBody CourseAssignmentRequestDto courseAssignmentRequestDto) {
        return adminService.assignInstructorToCourse(courseAssignmentRequestDto);
    }
}
