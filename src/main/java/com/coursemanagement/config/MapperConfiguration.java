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
import org.hibernate.collection.spi.LazyInitializable;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
import org.modelmapper.Condition;
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
    private final static Set<String> ALLOWED_PERSISTENCE_LOADING_STATES = Set.of("LOADED", "UNKNOWN");

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        addConfirmationTokenMapping(modelMapper);
        addRoleMapping(modelMapper);
        addCourseMapping(modelMapper);

        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull())
                .setPropertyCondition(lazyInitializationCondition())
                .setPreferNestedProperties(false);
        return modelMapper;
    }

    private static Condition<Object, Object> lazyInitializationCondition() {
        return mappingContext -> {
            final Object source = mappingContext.getSource();
            if (source instanceof LazyInitializable) {
                return isLoaded(source);
            }
            return true;
        };
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
            if (!isLoaded(entity)) {
                return null;
            }

            final Set<User> users = Optional.ofNullable(entity.getUserCourses())
                    .filter(MapperConfiguration::isLoaded)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(UserCourseEntity::getUserEntity)
                    .filter(MapperConfiguration::isLoaded)
                    .map(userEntity -> modelMapper.map(userEntity, User.class))
                    .collect(Collectors.toSet());

            return Course.builder()
                    .code(entity.getCode())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .users(users)
                    .build();
        };

        modelMapper.createTypeMap(CourseEntity.class, Course.class)
                .setConverter(entityToCourseMapping);
    }

    private static <T> boolean isLoaded(final T object) {
        final String loadedState = PersistenceUtilHelper.isLoaded(object).name();
        return ALLOWED_PERSISTENCE_LOADING_STATES.contains(loadedState);
    }
}
