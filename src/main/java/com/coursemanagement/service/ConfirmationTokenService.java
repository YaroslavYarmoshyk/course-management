package com.coursemanagement.service;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.UserDto;

public interface ConfirmationTokenService {

    ConfirmationToken createEmailConfirmationToken(final User user);

    ConfirmationToken createResetPasswordToken(final User user);

    ConfirmationToken getByTokenAndType(final String token, final TokenType type);

    ConfirmationToken confirmToken(final String token, final TokenType type);

    UserDto confirmUserByEmailToken(final String token);
}
