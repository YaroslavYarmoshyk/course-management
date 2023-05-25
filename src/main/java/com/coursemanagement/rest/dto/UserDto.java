package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.RoleName;
import com.coursemanagement.enumeration.UserStatus;

import java.util.Set;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserStatus status,
        Set<RoleName> roles
) {
}
