package com.coursemanagement.service.impl;

import com.coursemanagement.config.annotation.InstructorTestUser;
import com.coursemanagement.config.annotation.StudentTestUser;
import com.coursemanagement.config.extension.UserProviderExtension;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(value = {
        MockitoExtension.class,
        InstancioExtension.class
})
class CourseManagementServiceImplTest {
    @InjectMocks
    @Spy
    private CourseManagementServiceImpl courseManagementService;
    @Mock
    private UserService userService;
    @Mock
    private CourseService courseService;
    @Mock
    private LessonService lessonService;
    @Mock
    private MarkService markService;

    private static final int COURSE_LIMIT = 5;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(courseManagementService, "studentCourseLimit", COURSE_LIMIT);
    }

    @Nested
    @DisplayName("Test instructor assigment")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @ExtendWith(UserProviderExtension.class)
    class InstructorAssigmentTests {

        @Order(1)
        @Test
        void testShouldPass_ValidUserRole(@InstructorTestUser final User instructor) {
            final Course testCourse = Instancio.create(Course.class);

            doNothing().when(courseService).addUserToCourses(any(), anyCollection());
            doReturn(testCourse).when(courseService).getCourseByCode(any());
            when(userService.getUserById(instructor.getId())).thenReturn(instructor);
            courseManagementService.assignInstructorToCourse(instructor.getId(), testCourse.getCode());
        }

        @Order(2)
        @Test
        void testShouldThrowException_InvalidUserRole(@StudentTestUser final User instructor) {
            final Course emptyCourse = new Course();
            emptyCourse.setUsers(Set.of());
            when(userService.getUserById(instructor.getId())).thenReturn(instructor);

            assertThrows(SystemException.class, () -> courseManagementService.assignInstructorToCourse(instructor.getId(), 22332L));
        }
    }
}