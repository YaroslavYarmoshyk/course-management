package com.coursemanagement.integration.repository;

import com.coursemanagement.config.DatabaseSetupExtension;
import com.coursemanagement.config.properties.CourseTestDataProperties;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
@ExtendWith(DatabaseSetupExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableConfigurationProperties(CourseTestDataProperties.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseTestDataProperties courseTestDataProperties;
    private Long mathematicsCourseCode;
    private Long historyCourseCode;

    @BeforeEach
    void setUp() {
        mathematicsCourseCode = courseTestDataProperties.getMathematics().getCode();
        historyCourseCode = courseTestDataProperties.getHistory().getCode();
    }

    @Test
    @Order(1)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find course by code with fetched user courses")
    @Sql(value = "/scripts/add_users_to_courses.sql")
    void testFindCourseByCode_UserCourse_Is_Fetched() {
        final Optional<CourseEntity> courseEntityOptional = courseRepository.findByCode(mathematicsCourseCode);
        assertTrue(courseEntityOptional.isPresent());

        final CourseEntity courseEntity = courseEntityOptional.get();
        final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
        assertFalse(userCourseEntities.isEmpty());
    }

    @Test
    @Order(2)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find course by code with fetched users")
    @Sql(value = "/scripts/add_users_to_courses.sql")
    void testFindCourseByCode_Users_Are_Fetched() {
        final Optional<CourseEntity> courseEntityOptional = courseRepository.findByCode(mathematicsCourseCode);
        assertTrue(courseEntityOptional.isPresent());

        final CourseEntity courseEntity = courseEntityOptional.get();
        final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
        final Set<Long> userIds = userCourseEntities
                .stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        assertTrue(userIds.contains(2L));
    }

    @Test
    @Order(3)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find course by code with fetched user roles")
    @Sql(value = "/scripts/add_users_to_courses.sql")
    void testFindCourseByCode_User_Roles_Are_Fetched() {
        final Optional<CourseEntity> courseEntityOptional = courseRepository.findByCode(mathematicsCourseCode);
        assertTrue(courseEntityOptional.isPresent());

        final CourseEntity courseEntity = courseEntityOptional.get();
        final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
        final Set<Role> userRoles = userCourseEntities.stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getRoles)
                .flatMap(Collection::stream)
                .map(RoleEntity::getRole)
                .collect(Collectors.toSet());
        assertTrue(userRoles.contains(Role.INSTRUCTOR));
    }

    @Test
    @Order(4)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find all courses by codes with fetched user courses")
    @Sql(value = "/scripts/add_users_to_courses.sql")
    void testFindAllCoursesByCodeIn_UserCourses_Are_Fetched() {
        final Set<Long> courseCodes = Set.of(mathematicsCourseCode, historyCourseCode);
        final Set<UserCourseEntity> userCourseEntities = courseRepository.findAllByCodeIn(courseCodes).stream()
                .map(CourseEntity::getUserCourses)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertFalse(userCourseEntities.isEmpty());
    }
}
