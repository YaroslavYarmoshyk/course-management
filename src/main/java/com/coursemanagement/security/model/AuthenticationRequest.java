package com.coursemanagement.security.model;

import com.coursemanagement.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        String firstName,
        String lastName,
        @NotBlank @Email String email,
        String phone,
        @NotBlank String password
) {
    public AuthenticationRequest(User user) {
        this(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword()
        );
    }
}
