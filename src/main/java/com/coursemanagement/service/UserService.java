package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;

public interface UserService {

    User resolveCurrentUser();

    User getUserByEmail(final String email);

    User getUserById(final Long userId);

    User save(final User user);

    User assignRoleToUser(RoleAssignmentDto roleAssignmentDto);
}
