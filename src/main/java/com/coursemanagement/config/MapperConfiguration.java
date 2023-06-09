package com.coursemanagement.config;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
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

        Converter<Object, Object> lazyConverter = context -> {
            if (context.getSource() != null && PersistenceUtilHelper.isLoaded(context.getSource()).name().equals("LOADED")) {
                return context.getSource();
            }
            return null;
        };
        modelMapper.addConverter(lazyConverter);
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
            final Set<UserCourse> userCourses = entity.getUserCourses().stream()
                    .map(userCourseEntity -> new UserCourse(
                            userCourseEntity.getId(),
                            modelMapper.map(userCourseEntity.getUserEntity(), User.class),
                            new Course(entity.getCode(), entity.getTitle(), entity.getDescription(), Set.of()),
                            userCourseEntity.getStatus()
                    ))
                    .collect(Collectors.toSet());
            return Course.builder()
                    .code(entity.getCode())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .userCourses(userCourses)
                    .build();
        };
        final Converter<Course, CourseEntity> courseToEntityMapping = context -> {
            final Course course = context.getSource();
            final Set<UserCourseEntity> userCourseEntities = Optional.ofNullable(course.getUserCourses())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(userCourse -> new UserCourseEntity(userCourse.getUser().getId(), userCourse.getCourse().getCode()))
                    .collect(Collectors.toSet());
            return new CourseEntity()
                    .setCode(course.getCode())
                    .setTitle(course.getTitle())
                    .setDescription(course.getDescription())
                    .setUserCourses(userCourseEntities);
        };

        modelMapper.createTypeMap(CourseEntity.class, Course.class)
                .setConverter(entityToCourseMapping);
        modelMapper.createTypeMap(Course.class, CourseEntity.class)
                .setConverter(courseToEntityMapping);
    }
}
