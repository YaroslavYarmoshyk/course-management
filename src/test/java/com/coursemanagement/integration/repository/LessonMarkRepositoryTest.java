package com.coursemanagement.integration.repository;

import com.coursemanagement.config.DatabaseSetupExtension;
import com.coursemanagement.config.properties.UserTestDataProperties;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.LessonMarkRepository;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.repository.entity.LessonMarkEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(showSql = false)
@ExtendWith(DatabaseSetupExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableConfigurationProperties(UserTestDataProperties.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LessonMarkRepositoryTest {
    @Autowired
    private LessonMarkRepository lessonMarkRepository;
    @Autowired
    private UserTestDataProperties userTestDataProperties;
    private User student;
    private User instructor;

    @BeforeEach
    void setUp() {
        student = userTestDataProperties.getStudent();
        instructor = userTestDataProperties.getInstructor();
    }

    @Test
    @Order(1)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find lesson mark by id with fetched student")
    @Sql(value = "/scripts/add_lesson_marks.sql")
    void testFindLessonMarkById_Student_Is_Fetched() {
        final LessonMarkEntity lessonMarkEntity = lessonMarkRepository.findLessonMarkById(1L);
        assertNotNull(lessonMarkEntity);
        final UserEntity studentEntity = lessonMarkEntity.getStudent();
        assertNotNull(studentEntity);
        assertEquals(studentEntity.getId(), student.getId());
    }

    @Test
    @Order(2)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find lesson mark by id with fetched lesson")
    @Sql(value = "/scripts/add_lesson_marks.sql")
    void testFindLessonMarkById_Lesson_Is_Fetched() {
        final LessonMarkEntity lessonMarkEntity = lessonMarkRepository.findLessonMarkById(1L);
        assertNotNull(lessonMarkEntity);
        final LessonEntity lessonEntity = lessonMarkEntity.getLesson();
        assertNotNull(lessonEntity);
        assertEquals(lessonEntity.getTitle(), "Introduction to Algebra");
    }

    @Test
    @Order(3)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find lesson mark by id with fetched instructor")
    @Sql(value = "/scripts/add_lesson_marks.sql")
    void testFindLessonMarkById_Instructor_Is_Fetched() {
        final LessonMarkEntity lessonMarkEntity = lessonMarkRepository.findLessonMarkById(1L);
        assertNotNull(lessonMarkEntity);
        final UserEntity instructorEntity = lessonMarkEntity.getInstructor();
        assertNotNull(instructorEntity);
        assertEquals(instructorEntity.getId(), instructor.getId());
        assertEquals(instructorEntity.getEmail(), instructor.getEmail());
    }
}
