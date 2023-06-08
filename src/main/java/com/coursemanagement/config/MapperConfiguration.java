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
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Optional;
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

        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

    private static void addConfirmationTokenMapping(final ModelMapper modelMapper) {
        final Converter<Long, UserEntity> userIdToUserEntityConverter = context -> {
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
            return roleEntity.getRole();
        };
        modelMapper.createTypeMap(RoleEntity.class, Role.class)
                .setConverter(roleEntityToRoleConverter);
    }

    private static void addCourseMapping(final ModelMapper modelMapper) {
        final Converter<CourseEntity, Course> entityToCourseMapping = context -> {
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
        final Converter<Course, CourseEntity> courseToEntityMapping = context -> {
            final Course course = context.getSource();
            final CourseEntity courseEntity = new CourseEntity()
                    .setCode(course.getCode())
                    .setTitle(course.getTitle())
                    .setDescription(course.getDescription());
            final Set<UserCourseEntity> userCourseEntities = Optional.ofNullable(course.getUsers())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(user -> new UserEntity().setId(user.getId()))
                    .map(userEntity -> new UserCourseEntity(userEntity, courseEntity))
                    .collect(Collectors.toSet());
            courseEntity.setUsers(userCourseEntities);
            return courseEntity;
        };

        modelMapper.createTypeMap(CourseEntity.class, Course.class)
                .setConverter(entityToCourseMapping);
        modelMapper.createTypeMap(Course.class, CourseEntity.class)
                .setConverter(courseToEntityMapping);
    }
}
