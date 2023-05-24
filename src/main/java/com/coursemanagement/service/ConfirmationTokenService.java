package com.coursemanagement.service;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;

public interface ConfirmationTokenService {

    ConfirmationToken createEmailConfirmationToken(final User user);

    ConfirmationToken findByTokenAndType(final String token, final TokenType type);

    boolean isTokenValid(final ConfirmationToken token);

    void invalidateToken(final ConfirmationToken token);
}
