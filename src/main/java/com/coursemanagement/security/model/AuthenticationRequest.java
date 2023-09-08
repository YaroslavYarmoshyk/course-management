package com.coursemanagement.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull @Email String email,
        @NotNull String phone,
        @NotNull String password
) {
}
