package com.coursemanagement.config;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;


@Configuration
public class MapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        addConfirmationTokenMapping(modelMapper);
        addRoleMapping(modelMapper);
        addCourseMapping(modelMapper);
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

    private static void addCourseMapping(final ModelMapper modelMapper) {
        final Converter<CourseEntity, Course> entityToCourseMapping = context -> {
            if (context.getSource() == null) {
                return null;
            }
            final CourseEntity entity = context.getSource();
            final Set<User> users = entity.getUsers().stream()
                    .map(UserCourseEntity::getUserEntity)
                    .map(en -> modelMapper.map(en, User.class))
                    .collect(Collectors.toSet());
            final Course course = Course.builder()
                    .code(entity.getCode())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .build();
            course.setUsers(users);
            return course;
        };

        modelMapper.createTypeMap(CourseEntity.class, Course.class)
                .setConverter(entityToCourseMapping);
    }
}
