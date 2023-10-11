package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleAssignmentDto(
        @NotNull Long userId,
        @NotNull Set<Role> roles
) {
}
