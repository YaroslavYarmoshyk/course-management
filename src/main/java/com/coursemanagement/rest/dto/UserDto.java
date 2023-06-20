package com.coursemanagement.rest.dto;

import com.coursemanagement.model.User;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
    public UserDto(final User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
