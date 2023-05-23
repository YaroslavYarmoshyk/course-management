package com.coursemanagement.service;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;

public interface ConfirmationTokenService {

    ConfirmationToken createEmailConfirmationToken(final User user);

    boolean isConfirmationTokenValid(final String token, final TokenType type);

    void invalidateToken(ConfirmationToken token);
}
