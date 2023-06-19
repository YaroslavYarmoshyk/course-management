package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Role;

import java.util.Set;

public record RoleAssignmentDto(
        Long userId,
        Set<Role> roles
) {
}
