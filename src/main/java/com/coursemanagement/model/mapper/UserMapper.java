package com.coursemanagement.model.mapper;

import com.coursemanagement.enumeration.RoleName;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity modelToEntity(User user);

    @Mapping(target = "authorities", ignore = true)
    User entityToModel(UserEntity userEntity);

    default RoleEntity mapToRoleEntity(RoleName roleName) {
        return new RoleEntity(roleName);
    }

    default RoleName mapToRoleName(RoleEntity roleEntity) {
        return roleEntity.getName();
    }
}
