package com.coursemanagement.rest.dto;

import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.UserEntity;

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

    public UserInfoDto(final UserEntity userEntity) {
        this(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getPhone()
        );
    }
}
