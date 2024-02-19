package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserStatus status,
        Set<Role> roles
) {

    public UserDto(final User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getRoles()
        );
    }

    public UserDto(final UserEntity userEntity) {
        this(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getPhone(),
                userEntity.getStatus(),
                userEntity.getRoles().stream()
                        .map(RoleEntity::getRole)
                        .collect(Collectors.toSet())
        );
    }

}
