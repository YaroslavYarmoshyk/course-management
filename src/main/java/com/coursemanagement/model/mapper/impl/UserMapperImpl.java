package com.coursemanagement.model.mapper.impl;

import com.coursemanagement.model.User;
import com.coursemanagement.model.mapper.UserMapper;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toModel(final UserEntity userEntity) {
        return new User().setId(userEntity.getId())
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setEmail(userEntity.getEmail())
                .setPhone(userEntity.getPhone())
                .setPassword(userEntity.getPassword())
                .setStatus(userEntity.getStatus())
                .setRoles(
                        userEntity.getRoles().stream()
                                .map(RoleEntity::getName)
                                .collect(Collectors.toSet())
                );
    }

    @Override
    public UserEntity toEntity(final User user) {
        return new UserEntity().setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPhone(user.getPhone())
                .setPassword(user.getPassword())
                .setStatus(user.getStatus())
                .setRoles(
                        getRoleEntitiesFromUser(user)
                );
    }

    private static Set<RoleEntity> getRoleEntitiesFromUser(final User user) {
        return Optional.ofNullable(user.getRoles())
                .stream()
                .flatMap(Collection::stream)
                .map(RoleEntity::new)
                .collect(Collectors.toSet());
    }
}
