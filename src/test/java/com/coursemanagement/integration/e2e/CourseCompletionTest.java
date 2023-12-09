package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.COURSE_COMPLETION_ENDPOINT;
import static com.coursemanagement.util.Constants.MARK_ROUNDING_MODE;
import static com.coursemanagement.util.Constants.MARK_ROUNDING_SCALE;
import static com.coursemanagement.util.JwtTokenUtils.getAuthTokenRequestSpec;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.getModelIgnoringFields;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql(value = "/scripts/add_lessons_with_marks.sql")
public class CourseCompletionTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private MarkService markService;

    @TestFactory
    @DisplayName(value = "Test course completion flow")
    Stream<DynamicTest> testCourseCompletionFlow() {
        return Stream.of(
                dynamicTest("Test course completion request without student id",
                        () -> testBadCourseCompletionRequest(getModelIgnoringFields(CourseCompletionRequestDto.class, CourseCompletionRequestDto::studentId))),
                dynamicTest("Test course completion request without course code",
                        () -> testBadCourseCompletionRequest(getModelIgnoringFields(CourseCompletionRequestDto.class, CourseCompletionRequestDto::courseCode))),
                dynamicTest("Test course completion when all lessons are marked above allowed percent",
                        () -> testCompletedCourseWithAllLessonsMarkedAboveMinPercent(new CourseCompletionRequestDto(3L, 22324L))),
                dynamicTest("Test course completion when all lessons are marked equal to allowed percent",
                        () -> testCompletedCourseWithAllLessonsMarkedEqualMinPercent(new CourseCompletionRequestDto(3L, 34432L))),
                dynamicTest("Test course completion when all lessons are marked below allowed percent",
                        () -> testBadCourseCompletionRequest(new CourseCompletionRequestDto(3L, 56548L))),
                dynamicTest("Test course completion when not all lessons are marked",
                        () -> testBadCourseCompletionRequest(new CourseCompletionRequestDto(4L, 99831L)))
        );
    }

    private void testBadCourseCompletionRequest(final CourseCompletionRequestDto courseCompletionRequestDto) {
        getAuthTokenRequestSpec(FIRST_STUDENT, requestSpecification)
                .body(courseCompletionRequestDto)
                .post(COURSE_COMPLETION_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void testCompletedCourseWithAllLessonsMarkedAboveMinPercent(final CourseCompletionRequestDto courseCompletionRequestDto) {
        makeCourseCompletionRequest(courseCompletionRequestDto)
                .then()
                .spec(validResponseSpecification)
                .body(matchesJsonSchemaInClasspath("schemas/firstStudentMathematicsCourseCompletionResponseSchema.json"));

        final Long studentId = courseCompletionRequestDto.studentId();
        final Long courseCode = courseCompletionRequestDto.courseCode();
        final Course expectedCourse = courseService.getCourseByCode(courseCode);
        final UserCourse userCourse = userCourseService.getUserCourse(courseCompletionRequestDto.studentId(), courseCode);
        final Course actualCourse = userCourse.getCourse();

        assertEquals(expectedCourse.getCode(), actualCourse.getCode());
        assertEquals(expectedCourse.getSubject(), actualCourse.getSubject());
        assertEquals(expectedCourse.getDescription(), actualCourse.getDescription());
        assertEquals(UserCourseStatus.COMPLETED, userCourse.getStatus());
        assertNotNull(userCourse.getAccomplishmentDate());

        final CourseMark courseMark = markService.getStudentCourseMark(studentId, courseCode);
        assertEquals(BigDecimal.valueOf(4.70).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), courseMark.getMarkValue());
        assertEquals(Mark.EXCELLENT, courseMark.getMark());

        final Map<Long, BigDecimal> lessonMarks = courseMark.getLessonMarks();
        assertEquals(5, lessonMarks.size());
        assertEquals(BigDecimal.valueOf(5.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(1L));
        assertEquals(BigDecimal.valueOf(4.50).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(2L));
        assertEquals(BigDecimal.valueOf(5.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(3L));
        assertEquals(BigDecimal.valueOf(5.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(4L));
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(5L));
    }

    private void testCompletedCourseWithAllLessonsMarkedEqualMinPercent(final CourseCompletionRequestDto courseCompletionRequestDto) {
        makeCourseCompletionRequest(courseCompletionRequestDto)
                .then()
                .spec(validResponseSpecification)
                .body(matchesJsonSchemaInClasspath("schemas/firstStudentHistoryCourseCompletionResponseSchema.json"));

        final Long studentId = courseCompletionRequestDto.studentId();
        final Long courseCode = courseCompletionRequestDto.courseCode();
        final Course expectedCourse = courseService.getCourseByCode(courseCode);
        final UserCourse userCourse = userCourseService.getUserCourse(courseCompletionRequestDto.studentId(), courseCode);
        final Course actualCourse = userCourse.getCourse();

        assertEquals(expectedCourse.getCode(), actualCourse.getCode());
        assertEquals(expectedCourse.getSubject(), actualCourse.getSubject());
        assertEquals(expectedCourse.getDescription(), actualCourse.getDescription());
        assertEquals(UserCourseStatus.COMPLETED, userCourse.getStatus());
        assertNotNull(userCourse.getAccomplishmentDate());

        final CourseMark courseMark = markService.getStudentCourseMark(studentId, courseCode);
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), courseMark.getMarkValue());
        assertEquals(Mark.ABOVE_AVERAGE, courseMark.getMark());

        final Map<Long, BigDecimal> lessonMarks = courseMark.getLessonMarks();
        assertEquals(5, lessonMarks.size());
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(6L));
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(7L));
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(8L));
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(9L));
        assertEquals(BigDecimal.valueOf(4.00).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE), lessonMarks.get(10L));
    }

    private Response makeCourseCompletionRequest(final CourseCompletionRequestDto courseCompletionRequestDto) {
        final User user = userService.getUserById(courseCompletionRequestDto.studentId());
        return getAuthTokenRequestSpec(user, requestSpecification)
                .body(courseCompletionRequestDto)
                .post(COURSE_COMPLETION_ENDPOINT);
    }

}
