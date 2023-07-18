package com.coursemanagement.service;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;

public interface ConfirmationTokenService {

    ConfirmationToken createEmailConfirmationToken(final User user);

    ConfirmationToken createResetPasswordToken(final User user);

    ConfirmationToken confirmToken(final String token, final TokenType type);

    User confirmUserByEmailToken(final String token);
}
