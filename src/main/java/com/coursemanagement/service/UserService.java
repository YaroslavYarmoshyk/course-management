package com.coursemanagement.service;

import com.coursemanagement.model.User;

public interface UserService {

    User resolveCurrentUser();

    User getUserByEmail(final String email);

    User getUserById(final Long userId);

    User activateById(final Long userId);

    User save(final User user);
}
