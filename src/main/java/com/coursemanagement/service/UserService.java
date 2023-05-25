package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;

public interface UserService {
    User findByEmail(final String email);

    User findById(final Long id);

    void confirmUserByEmailToken(final String token);

    User save(final User user);

    boolean isEmailAlreadyRegistered(final String email);

    UserDto assignRole(RoleAssignmentDto roleAssignmentDto);
}
