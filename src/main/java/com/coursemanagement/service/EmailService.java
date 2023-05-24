package com.coursemanagement.service;

import com.coursemanagement.model.User;

public interface EmailService {

    void sendEmailConfirmation(final User user, final String token);
}
