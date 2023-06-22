package com.coursemanagement.rest.dto;

import com.coursemanagement.model.User;

public record UserInfoDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
    public UserInfoDto(final User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
