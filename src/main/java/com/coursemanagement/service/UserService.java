package com.coursemanagement.service;

import com.coursemanagement.model.User;

public interface UserService {
    User findByEmail(final String email);

    void confirmUserEmailByToken(final String token);

    User save(final User user);

    boolean isEmailAlreadyRegistered(final String email);
}
