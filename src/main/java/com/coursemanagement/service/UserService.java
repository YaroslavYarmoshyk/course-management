package com.coursemanagement.service;

import com.coursemanagement.model.User;

import java.util.Set;

public interface UserService {

    User resolveCurrentUser();

    User getUserByEmail(final String email);

    User getUserById(final Long userId);

    Set<User> getAllUsers();

    User activateById(final Long userId);

    User save(final User user);
}
