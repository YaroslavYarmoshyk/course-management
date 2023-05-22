package com.coursemanagement.security.dto;

public record AuthenticationRequestDto(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password
) {
    public AuthenticationRequestDto(final String email, final String password) {
        this(null, null, email, null, password);
    }
}
