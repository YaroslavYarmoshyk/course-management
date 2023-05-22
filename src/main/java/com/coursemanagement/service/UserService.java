package com.coursemanagement.service;

import com.coursemanagement.model.User;

public interface UserService {
    User findByEmail(final String email);

    User save(final User user);

    boolean isEmailAlreadyRegistered(final String email);
}
