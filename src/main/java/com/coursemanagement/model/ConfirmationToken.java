package com.coursemanagement.model;

import com.coursemanagement.enumeration.TokenType;

import java.time.LocalDateTime;

public record ConfirmationToken(
        Long id,
        Long userId,
        TokenType type,
        String token,
        LocalDateTime expirationDate,
        boolean active) {
}
