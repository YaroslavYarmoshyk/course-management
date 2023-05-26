package com.coursemanagement.rest.dto;

import com.coursemanagement.model.User;

public record UserCourseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
    public UserCourseDto(final User user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone());
    }
}
