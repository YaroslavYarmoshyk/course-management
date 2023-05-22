package com.coursemanagement.security.model;

public record AuthenticationRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password
) {
}
