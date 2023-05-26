package com.coursemanagement.config;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        addConfirmationTokenMapping(modelMapper);
        addRoleMapping(modelMapper);
        return modelMapper;
    }

    private static void addConfirmationTokenMapping(final ModelMapper modelMapper) {
        final Converter<Long, UserEntity> userIdToUserEntityConverter = context -> {
            if (context.getSource() == null) {
                return null;
            }
            final UserEntity userEntity = new UserEntity();
            userEntity.setId(context.getSource());
            return userEntity;
        };
        modelMapper.createTypeMap(ConfirmationToken.class, ConfirmationTokenEntity.class)
                .addMappings(mapper -> mapper.using(userIdToUserEntityConverter)
                        .map(ConfirmationToken::getUserId, ConfirmationTokenEntity::setUserEntity));
    }

    private static void addRoleMapping(final ModelMapper modelMapper) {
        final Converter<RoleEntity, Role> roleEntityToRoleConverter = context -> {
            final RoleEntity roleEntity = context.getSource();
            if (roleEntity == null) {
                return null;
            }
            return roleEntity.getRole();
        };
        modelMapper.createTypeMap(RoleEntity.class, Role.class)
                .setConverter(roleEntityToRoleConverter);
    }
}
