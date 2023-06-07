package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;

public interface UserService {

    User resolveCurrentUser();

    User getByEmail(final String email);

    User getById(final Long id);

    void confirmUserByEmailToken(final String token);

    User save(final User user);

    UserDto assignRole(RoleAssignmentDto roleAssignmentDto);
}
