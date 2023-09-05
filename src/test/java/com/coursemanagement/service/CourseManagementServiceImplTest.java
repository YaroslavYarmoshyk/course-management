package com.coursemanagement.service;

import com.coursemanagement.config.properties.CourseProperties;
import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.service.impl.CourseManagementServiceImpl;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.Constants.*;
import static com.coursemanagement.util.TestDataUtils.*;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.*;
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
    private UserCourseService userCourseService;
    @Mock
    private LessonService lessonService;
    @Mock
    private MarkService markService;
    @Mock
    private CourseProperties courseProperties;
    private static final int COURSE_LIMIT = 5;
    private static final BigDecimal MIN_PASSING_PERCENTAGE = BigDecimal.valueOf(80);

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
            assertEquals(expectedRoleCountMap.getOrDefault(Role.STUDENT, 0L), responseDto.students().size());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Test enroll student in courses")
    class TestStudentCourseEnrollment {

        @Order(1)
        @TestFactory
        @DisplayName("Test validation failure throws exception")
        Stream<DynamicTest> testEnrollStudentCourseAccessControl_ThrowException() {
            return Stream.of(
                    dynamicTest("Requested by admin for user without roles",
                            () -> testStudentEnrollmentAccessThrowsException(ADMIN, NEW_USER, "Only students can enroll courses")),
                    dynamicTest("Requested by admin for instructor",
                            () -> testStudentEnrollmentAccessThrowsException(ADMIN, INSTRUCTOR, "Only students can enroll courses")),
                    dynamicTest("Requested by instructor for student",
                            () -> testStudentEnrollmentAccessThrowsException(INSTRUCTOR, FIRST_STUDENT, "Access denied")),
                    dynamicTest("Requested by student for another student",
                            () -> testStudentEnrollmentAccessThrowsException(FIRST_STUDENT, SECOND_STUDENT, "Access denied")),
                    dynamicTest("Exceeded course limit at the same time",
                            () -> testCourseEnrollmentLimitExceededThrowsException(Set.of(10000L, 100001L)))
            );
        }

        private void testStudentEnrollmentAccessThrowsException(final User requestedByUser, final User requestedForUser, final String expectedMessage) {
            final var requestDto = new StudentEnrollInCourseRequestDto(requestedForUser.getId(), Set.of());
            when(userService.getUserById(requestedForUser.getId())).thenReturn(requestedForUser);
            when(userService.resolveCurrentUser()).thenReturn(requestedByUser);

            assertThrowsWithMessage(
                    () -> courseManagementService.enrollStudentInCourses(requestDto),
                    SystemException.class,
                    expectedMessage
            );
        }

        private void testCourseEnrollmentLimitExceededThrowsException(final Set<Long> requestedCourseCodes) {
            final Set<UserCourse> alreadyTakenCourses = getAlreadyTakenCourses();
            final Set<Course> foundRequestedCourses = requestedCourseCodes.stream()
                    .map(code -> Course.builder().code(code).build())
                    .collect(Collectors.toSet());
            final Long studentId = FIRST_STUDENT.getId();
            final var requestDto = new StudentEnrollInCourseRequestDto(studentId, requestedCourseCodes);
            when(userCourseService.getUserCoursesByUserId(studentId)).thenReturn(alreadyTakenCourses);
            when(courseService.getCoursesByCodes(anyCollection())).thenReturn(foundRequestedCourses);
            when(userService.getUserById(studentId)).thenReturn(FIRST_STUDENT);
            when(userService.resolveCurrentUser()).thenReturn(FIRST_STUDENT);

            assertThrows(SystemException.class, () -> courseManagementService.enrollStudentInCourses(requestDto));
        }

        @Order(2)
        @TestFactory
        @DisplayName("Test students enrollment success flow")
        Stream<DynamicTest> testEnrollStudentInCourse_Success() {
            final Set<UserCourse> alreadyTakenCourses = getAlreadyTakenCourses();
            final int alreadyTakenCourseCount = alreadyTakenCourses.size();

            when(userCourseService.getUserCoursesByUserId(anyLong())).thenReturn(alreadyTakenCourses);
            when(courseProperties.getStudentCourseLimit()).thenReturn(COURSE_LIMIT);

            return Stream.of(
                    dynamicTest("Requested one new course in addition to " + alreadyTakenCourseCount + " existing",
                            () -> testRequestedEnrollableCourseCount(Set.of(10000L), Set.of(10000L))),
                    dynamicTest("Requested two new courses, where one of them is not found in addition to " + alreadyTakenCourseCount + " existing",
                            () -> testRequestedEnrollableCourseCount(Set.of(10000L, 100001L), Set.of(10000L))),
                    dynamicTest("Requested two new courses that were not found",
                            () -> testRequestedEnrollableCourseCount(Set.of(10000L, 100001L), Set.of()))
            );
        }

        private void testRequestedEnrollableCourseCount(final Set<Long> requestedCourseCodes, final Set<Long> foundRequestedCourseCodes) {
            final var requestDto = new StudentEnrollInCourseRequestDto(FIRST_STUDENT.getId(), requestedCourseCodes);
            final Long studentId = FIRST_STUDENT.getId();
            final Set<Course> foundRequestedCourses = foundRequestedCourseCodes.stream()
                    .map(code -> Course.builder().code(code).build())
                    .collect(Collectors.toSet());
            when(courseService.getCoursesByCodes(anyCollection())).thenReturn(foundRequestedCourses);
            when(userService.getUserById(studentId)).thenReturn(FIRST_STUDENT);
            when(userService.resolveCurrentUser()).thenReturn(FIRST_STUDENT);

            final StudentEnrollInCourseResponseDto responseDto = courseManagementService.enrollStudentInCourses(requestDto);

            verify(courseService).addUserToCourses(FIRST_STUDENT, foundRequestedCourseCodes);
            reset(courseService);
            assertNotNull(responseDto);
            assertEquals(FIRST_STUDENT.getId(), responseDto.studentId());
        }

        private Set<UserCourse> getAlreadyTakenCourses() {
            return Instancio.ofSet(UserCourse.class).size(COURSE_LIMIT - 1)
                    .supply(field(UserCourse::getUser), () -> FIRST_STUDENT)
                    .supply(field(UserCourse::getCourse), () -> Instancio.of(COURSE_TEST_MODEL)
                            .onComplete(field(Course::getUsers), (Set<User> users) -> users.add(FIRST_STUDENT))
                            .create())
                    .set(field(UserCourse::getStatus), UserCourseStatus.STARTED)
                    .create();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Test student course completion")
    class TestStudentCourseCompletion {
        @Captor
        private ArgumentCaptor<UserCourse> userCourseCaptor;
        private static final BigDecimal MIN_PASSING_AVG_LESSONS_MARK = MIN_PASSING_PERCENTAGE
                .divide(HUNDRED, MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE)
                .multiply(Mark.EXCELLENT.getValue());
        private static final BigDecimal BELLOW_MIN_PASSING_AVG_LESSONS_MARK = MIN_PASSING_AVG_LESSONS_MARK.subtract(BigDecimal.ONE);
        private static final BigDecimal EXCELLENT_AVG_LESSONS_MARK = Mark.EXCELLENT.getValue();

        @Order(1)
        @TestFactory
        @DisplayName("Test student course completion possibility")
        Stream<DynamicTest> testCompleteStudentCourseValidation() {
            final UserCourse userCourse = getRandomUserCourseByUser(FIRST_STUDENT);
            final Course course = userCourse.getCourse();
            final Long studentId = FIRST_STUDENT.getId();
            final Long courseCode = course.getCode();
            final Set<Lesson> lessons = getRandomLessonsByCourse(course);

            final CourseMark notCompletedCourseMark = getCourseMark(studentId, lessons, EXCELLENT_AVG_LESSONS_MARK, false);
            final CourseMark excellentCourseMark = getCourseMark(studentId, lessons, EXCELLENT_AVG_LESSONS_MARK, true);
            final CourseMark minPassCourseMark = getCourseMark(studentId, lessons, MIN_PASSING_AVG_LESSONS_MARK, true);
            final CourseMark belowAverageCourseMark = getCourseMark(studentId, lessons, BELLOW_MIN_PASSING_AVG_LESSONS_MARK, true);

            when(userCourseService.getUserCourse(anyLong(), anyLong())).thenReturn(userCourse);
            when(lessonService.getLessonsPerCourse(anyLong())).thenReturn(lessons);
            when(courseProperties.getCoursePassingPercentage()).thenReturn(MIN_PASSING_PERCENTAGE);
            doReturn(userCourse).when(userCourseService).saveUserCourse(userCourse);

            return Stream.of(
                    dynamicTest("Not all lessons are graded", () -> testCourseCompletionValidation(studentId, courseCode, notCompletedCourseMark, "Cannot complete course, not all lessons are graded")),
                    dynamicTest("Final course mark is bellow min course passing mark", () -> testCourseCompletionValidation(studentId, courseCode, belowAverageCourseMark, "Cannot complete course, minimum passing percentage is: 80 but student has: 60.00")),
                    dynamicTest("Final course mark is min course passing mark", () -> testCourseCompletion(studentId, courseCode, minPassCourseMark)),
                    dynamicTest("Final course mark is above course passing mark", () -> testCourseCompletion(studentId, courseCode, excellentCourseMark))
            );
        }

        private CourseMark getCourseMark(final Long studentId, final Set<Lesson> lessons, final BigDecimal avgLessonsMark, final boolean allLessonGraded) {
            final Long courseCode = lessons.stream().findFirst().map(Lesson::getCourse).orElseThrow().getCode();
            final Map<Long, BigDecimal> lessonMarks = lessons.stream()
                    .skip(allLessonGraded ? BigDecimal.ZERO.intValue() : BigDecimal.ONE.intValue())
                    .collect(Collectors.toMap(
                            Lesson::getId,
                            lesson -> avgLessonsMark
                    ));
            return CourseMark.courseMark()
                    .withStudentId(studentId)
                    .withCourseCode(courseCode)
                    .withLessonMarks(lessonMarks)
                    .withMarkValue(avgLessonsMark)
                    .withMark(Mark.of(avgLessonsMark))
                    .build();
        }

        private void testCourseCompletionValidation(final Long studentId, final Long courseCode, final CourseMark courseMark, final String expectedErrorMessage) {
            when(markService.getStudentCourseMark(anyLong(), anyLong())).thenReturn(courseMark);

            assertThrowsWithMessage(
                    () -> courseManagementService.completeStudentCourse(new CourseCompletionRequestDto(studentId, courseCode)),
                    SystemException.class,
                    expectedErrorMessage
            );
        }

        private void testCourseCompletion(final Long studentId, final Long courseCode, final CourseMark courseMark) {
            when(markService.getStudentCourseMark(anyLong(), anyLong())).thenReturn(courseMark);

            courseManagementService.completeStudentCourse(new CourseCompletionRequestDto(studentId, courseCode));

            verify(userCourseService, atLeastOnce()).saveUserCourse(userCourseCaptor.capture());

            final UserCourse savedUserCourse = userCourseCaptor.getValue();
            assertEquals(UserCourseStatus.COMPLETED, savedUserCourse.getStatus());
            assertNotNull(savedUserCourse.getAccomplishmentDate());
        }
    }
}