package com.coursemanagement.service;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.service.impl.CourseManagementServiceImpl;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.ADMIN;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static com.coursemanagement.util.TestDataUtils.NEW_USER;
import static com.coursemanagement.util.TestDataUtils.RANDOM_COURSE;
import static com.coursemanagement.util.TestDataUtils.SECOND_STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Test instructor assigment")
    class InstructorAssigmentTests {

        @Order(1)
        @Test
        @DisplayName("Test assign user with role instructor to course")
        void testShouldPassInstructorAssigment_ValidUserRole() {
            doNothing().when(courseService).addUserToCourses(any(), anyCollection());
            doReturn(RANDOM_COURSE).when(courseService).getCourseByCode(any());
            when(userService.getUserById(INSTRUCTOR.getId())).thenReturn(INSTRUCTOR);

            courseManagementService.assignInstructorToCourse(INSTRUCTOR.getId(), RANDOM_COURSE.getCode());

            verify(courseService).addUserToCourses(INSTRUCTOR, Set.of(RANDOM_COURSE.getCode()));
        }

        @Order(2)
        @Test
        @DisplayName("Test throwing exception when expected user is not instructor")
        void testShouldThrowException_InvalidUserRole() {
            when(userService.getUserById(FIRST_STUDENT.getId())).thenReturn(FIRST_STUDENT);

            final SystemException exception = assertThrows(SystemException.class, () -> courseManagementService.assignInstructorToCourse(FIRST_STUDENT.getId(), RANDOM_COURSE.getCode()));
            assertEquals("Cannot assign user to the course, the user is not an instructor", exception.getMessage());
        }

        @Order(3)
        @Test
        @DisplayName("Test grouping users by role")
        void testGroupingUsersByRolesAfterAssigment() {
            final Map<Role, Long> expectedRoleCountMap = RANDOM_COURSE.getUsers().stream()
                    .flatMap(user -> user.getRoles().stream())
                    .collect(Collectors.groupingBy(
                            role -> role,
                            Collectors.counting()
                    ));
            when(userService.getUserById(INSTRUCTOR.getId())).thenReturn(INSTRUCTOR);
            doNothing().when(courseService).addUserToCourses(any(), anyCollection());
            doReturn(RANDOM_COURSE).when(courseService).getCourseByCode(any());

            final var responseDto = courseManagementService.assignInstructorToCourse(INSTRUCTOR.getId(), RANDOM_COURSE.getCode());

            assertEquals(expectedRoleCountMap.get(Role.INSTRUCTOR), responseDto.instructors().size());
            assertEquals(expectedRoleCountMap.get(Role.STUDENT), responseDto.students().size());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Test enroll student in courses")
    class TestStudentCourseEnrollment {

        @Order(1)
        @TestFactory
        @DisplayName("Test throwing exception when access control is not satisfied")
        Stream<DynamicTest> testEnrollStudentCourseAccessControl_ThrowException() {
            return Stream.of(
                    dynamicTest("Requested by admin for user without roles",
                            () -> testStudentEnrollmentThrowsException(ADMIN, NEW_USER, "Only students can enroll courses")),
                    dynamicTest("Requested by admin for instructor",
                            () -> testStudentEnrollmentThrowsException(ADMIN, NEW_USER, "Only students can enroll courses")),
                    dynamicTest("Requested by instructor for student",
                            () -> testStudentEnrollmentThrowsException(INSTRUCTOR, FIRST_STUDENT, "Access denied")),
                    dynamicTest("Requested by student for another student",
                            () -> testStudentEnrollmentThrowsException(FIRST_STUDENT, SECOND_STUDENT, "Access denied"))
            );
        }

        private void testStudentEnrollmentThrowsException(final User requestedByUser, final User requestedForUser, final String expectedMessage) {
            when(userService.getUserById(requestedForUser.getId())).thenReturn(requestedForUser);
            when(userService.resolveCurrentUser()).thenReturn(requestedByUser);
            final var requestDto = new StudentEnrollInCourseRequestDto(requestedForUser.getId(), Set.of());

            assertThrowsWithMessage(
                    () -> courseManagementService.enrollStudentInCourses(requestDto),
                    SystemException.class,
                    expectedMessage
            );
        }
    }
}