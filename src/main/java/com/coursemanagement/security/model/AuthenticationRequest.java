package com.coursemanagement.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        String firstName,
        String lastName,
        @NotBlank @Email String email,
        String phone,
        @NotBlank String password
) {
}
