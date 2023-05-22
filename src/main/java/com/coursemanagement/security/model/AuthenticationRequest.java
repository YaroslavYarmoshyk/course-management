package com.coursemanagement.security.model;

public record AuthenticationRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password
) {
    public AuthenticationRequest(final String email, final String password) {
        this(null, null, email, null, password);
    }
}
