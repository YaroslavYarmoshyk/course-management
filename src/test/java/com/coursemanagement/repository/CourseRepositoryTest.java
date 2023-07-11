package com.coursemanagement.repository;

import com.coursemanagement.config.DatabaseSetupExtension;
import com.coursemanagement.config.UserProperties;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursemanagement.TestUtils.MATHEMATICS_COURSE_CODE;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@ExtendWith(DatabaseSetupExtension.class)
@EnableConfigurationProperties(UserProperties.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test getting course by code with users")
    @Sql(value = "/scripts/add_users_to_courses.sql")
    void getCourseWithUsersByCode() {
        final Optional<CourseEntity> courseEntityOptional = courseRepository.findByCode(MATHEMATICS_COURSE_CODE);
        assertTrue(courseEntityOptional.isPresent());

        final CourseEntity courseEntity = courseEntityOptional.get();
        assertEquals(MATHEMATICS_COURSE_CODE, courseEntity.getCode());
        final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
        assertFalse(userCourseEntities.isEmpty());

        final Set<Long> userIds = userCourseEntities
                .stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        assertTrue(userIds.contains(2L));

        final Set<Role> userRoles = userCourseEntities.stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getRoles)
                .flatMap(Collection::stream)
                .map(RoleEntity::getRole)
                .collect(Collectors.toSet());
        assertTrue(userRoles.contains(Role.INSTRUCTOR));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test getting course by code with users")
    @Sql(value = "/scripts/add_users_to_courses.sql")
    void getCoursesWithUserCoursesByCodeIn() {
        final Optional<CourseEntity> courseEntityOptional = courseRepository.findByCode(MATHEMATICS_COURSE_CODE);
        assertTrue(courseEntityOptional.isPresent());

        final CourseEntity courseEntity = courseEntityOptional.get();
        final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
        assertFalse(userCourseEntities.isEmpty());

        final Set<Long> userIds = userCourseEntities
                .stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        assertTrue(userIds.contains(2L));

        final Set<Role> userRoles = userCourseEntities.stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getRoles)
                .flatMap(Collection::stream)
                .map(RoleEntity::getRole)
                .collect(Collectors.toSet());
        assertTrue(userRoles.contains(Role.INSTRUCTOR));
    }
}