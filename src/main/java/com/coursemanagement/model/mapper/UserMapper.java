package com.coursemanagement.model.mapper;

import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.UserEntity;

public interface UserMapper {
    User toModel(final UserEntity userEntity);

    UserEntity toEntity(final User user);
}
