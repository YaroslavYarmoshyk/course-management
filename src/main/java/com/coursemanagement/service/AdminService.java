package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.RoleAssignmentDto;

public interface AdminService {

    User assignRoleToUser(RoleAssignmentDto roleAssignmentDto);

    CourseAssignmentResponseDto assignInstructorToCourse(final CourseAssignmentRequestDto courseAssignmentRequestDto);
}
