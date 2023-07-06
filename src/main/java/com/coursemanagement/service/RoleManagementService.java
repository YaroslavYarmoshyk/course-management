package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;

public interface RoleManagementService {

    User assignRoleToUser(final RoleAssignmentDto roleAssignmentDto);
}
