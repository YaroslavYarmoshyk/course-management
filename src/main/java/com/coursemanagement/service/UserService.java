package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserInfoDto;

public interface UserService {

    User resolveCurrentUser();

    User getByEmail(final String email);

    User getById(final Long userId);

    void confirmUserByEmailToken(final String token);

    User save(final User user);

    UserInfoDto assignRole(RoleAssignmentDto roleAssignmentDto);
}
