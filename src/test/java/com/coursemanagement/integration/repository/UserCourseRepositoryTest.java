package com.coursemanagement.integration.repository;

import com.coursemanagement.config.annotation.RepositoryTest;
import com.coursemanagement.config.properties.CourseTestDataProperties;
import com.coursemanagement.config.properties.UserTestDataProperties;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
@EnableConfigurationProperties(value = {UserTestDataProperties.class, CourseTestDataProperties.class})
@Sql("/scripts/add_users_to_courses.sql")
class UserCourseRepositoryTest {
    @Autowired
    private UserCourseRepository userCourseRepository;
    @Autowired
    private UserTestDataProperties userTestDataProperties;
    @Autowired
    private CourseTestDataProperties courseTestDataProperties;
    private User student;
    private Course history;

    @BeforeEach
    void setUp() {
        history = courseTestDataProperties.getHistory();
        student = userTestDataProperties.getStudent();
    }

    @Test
    @Order(1)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user courses by user id with fetched user")
    void testFindUserCoursesByUserId_User_Is_Fetched() {
        final List<UserCourseEntity> userCoursesEntities = userCourseRepository.findByUserId(student.getId());
        final Set<String> userEmails = userCoursesEntities.stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getEmail)
                .collect(Collectors.toSet());
        assertTrue(userEmails.contains(student.getEmail()));
    }

    @Test
    @Order(2)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user courses by user id with fetched course")
    void testFindUserCoursesByUserId_Course_Is_Fetched() {
        final List<UserCourseEntity> userCoursesEntities = userCourseRepository.findByUserId(student.getId());
        final Set<String> courseDescriptions = userCoursesEntities.stream()
                .map(UserCourseEntity::getCourse)
                .map(CourseEntity::getDescription)
                .collect(Collectors.toSet());
        assertTrue(courseDescriptions.contains(history.getDescription()));
    }

    @Test
    @Order(3)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user courses by course code with fetched students")
    void testFindStudentsByCourseCode_Users_Are_Fetched() {
        final List<UserCourseEntity> userCoursesEntities = userCourseRepository.findStudentsByCourseCode(history.getCode());
        final Set<UserEntity> userEntities = userCoursesEntities.stream()
                .map(UserCourseEntity::getUser)
                .collect(Collectors.toSet());
        assertFalse(userEntities.isEmpty());
    }

    @Test
    @Order(4)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find students by course code should not contain other users")
    void testFindStudentsByCourseCode_Not_Students() {
        final List<UserCourseEntity> userCoursesEntities = userCourseRepository.findStudentsByCourseCode(history.getCode());
        final Set<UserEntity> userEntities = userCoursesEntities.stream()
                .map(UserCourseEntity::getUser)
                .filter(userEntity -> !isStudent(userEntity))
                .collect(Collectors.toSet());
        assertTrue(userEntities.isEmpty());
    }

    @Test
    @Order(5)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find students by course code should contain only students")
    void testFindStudentsByCourseCode_Only_Students() {
        final List<UserCourseEntity> userCoursesEntities = userCourseRepository.findStudentsByCourseCode(history.getCode());
        final Set<UserEntity> userEntities = userCoursesEntities.stream()
                .map(UserCourseEntity::getUser)
                .filter(UserCourseRepositoryTest::isStudent)
                .collect(Collectors.toSet());
        assertFalse(userEntities.isEmpty());
    }

    @Test
    @Order(6)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user course by user id and course code with fetched user")
    void testFindUserCourseByUserIdAndCourseCode_User_Is_Fetched() {
        final Optional<UserCourseEntity> userCoursesEntity = userCourseRepository.findByUserIdAndCourseCode(
                student.getId(),
                history.getCode()
        );
        assertTrue(userCoursesEntity.isPresent());
        assertEquals(userCoursesEntity.get().getUser().getEmail(), student.getEmail());
    }

    @Test
    @Order(7)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user course by user id and course code with fetched course")
    void testFindUserCourseByUserIdAndCourseCode_Course_Is_Fetched() {
        final Optional<UserCourseEntity> userCoursesEntity = userCourseRepository.findByUserIdAndCourseCode(
                student.getId(),
                history.getCode()
        );
        assertTrue(userCoursesEntity.isPresent());
        assertEquals(userCoursesEntity.get().getCourse().getDescription(), history.getDescription());
    }

    private static boolean isStudent(final UserEntity userEntity) {
        return userEntity.getRoles().stream()
                .map(RoleEntity::getRole)
                .collect(Collectors.toSet())
                .contains(Role.STUDENT);
    }
}
