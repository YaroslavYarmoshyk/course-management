package com.coursemanagement.service;

import com.coursemanagement.model.User;

public interface UserService {
    User findByEmail(final String email);
}
