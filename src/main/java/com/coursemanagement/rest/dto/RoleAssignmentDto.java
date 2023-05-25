package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.RoleName;

import java.util.Set;

public record RoleAssignmentDto(Long userId, Set<RoleName> roles) {
}
