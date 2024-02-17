package com.coursemanagement.service;

import com.coursemanagement.model.User;

import java.util.List;

public interface UserService {

    User resolveCurrentUser();

    User getUserByEmail(final String email);

    User getUserById(final Long userId);

    List<User> getAllUsers();

    User activateById(final Long userId);

    User save(final User user);
}
