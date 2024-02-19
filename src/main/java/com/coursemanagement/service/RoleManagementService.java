package com.coursemanagement.service;

import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;

public interface RoleManagementService {

    UserDto assignRoleToUser(final RoleAssignmentDto roleAssignmentDto);
}
