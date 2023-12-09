package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.instancio.GetMethodSelector;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.FEEDBACK_SUBMISSION_ENDPOINT;
import static com.coursemanagement.util.JwtTokenUtils.getAuthTokenRequestSpec;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql(value = "/scripts/add_users_to_courses.sql")
public class FeedbackSubmissionTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCourseService userCourseService;

    @Order(1)
    @TestFactory
    Stream<DynamicTest> testBadFeedbackSubmissionFlow() {
        final var defaultFeedbackDto = new FeedbackRequestDto(INSTRUCTOR.getId(), FIRST_STUDENT.getId(), 22324L, "Good Job!");
        return Stream.of(
                dynamicTest("Test submission by a new user",
                        () -> testUnauthorizedFeedbackSubmission(NEW_USER, defaultFeedbackDto)),
                dynamicTest("Test submission by a student",
                        () -> testUnauthorizedFeedbackSubmission(FIRST_STUDENT, defaultFeedbackDto)),
                dynamicTest("Test submission by an instructor using the student's ID as the instructor ID",
                        () -> testUnauthorizedFeedbackSubmission(FIRST_STUDENT, new FeedbackRequestDto(FIRST_STUDENT.getId(), FIRST_STUDENT.getId(), 22324L, "Good Job!"))),
                dynamicTest("Test submission by an instructor for non associated with course user",
                        () -> testUnauthorizedFeedbackSubmission(INSTRUCTOR, new FeedbackRequestDto(INSTRUCTOR.getId(), FIRST_STUDENT.getId(), 22324L, "Good Job!"))),
                dynamicTest("Test submission without specifying an instructor",
                        () -> testBadFeedbackSubmission(getFeedbackDtoIgnoring(FeedbackRequestDto::instructorId))),
                dynamicTest("Test submission without specifying a student",
                        () -> testBadFeedbackSubmission(getFeedbackDtoIgnoring(FeedbackRequestDto::studentId))),
                dynamicTest("Test submission without specifying a course",
                        () -> testBadFeedbackSubmission(getFeedbackDtoIgnoring(FeedbackRequestDto::courseCode))),
                dynamicTest("Test submission without feedback",
                        () -> testBadFeedbackSubmission(getFeedbackDtoIgnoring(FeedbackRequestDto::feedback)))
        );
    }

    @Order(2)
    @TestFactory
    Stream<DynamicTest> testValidFeedbackSubmissionFlow() {
        final var defaultFeedbackDto = new FeedbackRequestDto(INSTRUCTOR.getId(), FIRST_STUDENT.getId(), 34432L, "Good Job!");
        return Stream.of(
                dynamicTest("Test submission by an instructor",
                        () -> testValidFeedbackSubmission(INSTRUCTOR, defaultFeedbackDto)),
                dynamicTest("Test submission by an admin",
                        () -> testValidFeedbackSubmission(ADMIN, defaultFeedbackDto))
        );
    }

    private void testUnauthorizedFeedbackSubmission(final User user, final FeedbackRequestDto feedbackRequestDto) {
        makeFeedbackSubmissionRequest(user, feedbackRequestDto)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testBadFeedbackSubmission(final FeedbackRequestDto feedbackRequestDto) {
        makeFeedbackSubmissionRequest(INSTRUCTOR, feedbackRequestDto)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void testValidFeedbackSubmission(final User user, final FeedbackRequestDto feedbackRequestDto) {
        final FeedbackResponseDto feedbackResponseDto = makeFeedbackSubmissionRequest(user, feedbackRequestDto)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/feedbackSubmissionResponseSchema.json"))
                .extract()
                .as(FeedbackResponseDto.class);

        final Long studentId = feedbackRequestDto.studentId();
        final User feedbackSubmitter = userService.getUserById(feedbackRequestDto.instructorId());
        final User student = userService.getUserById(studentId);
        final UserCourse userCourse = userCourseService.getUserCourse(studentId, feedbackRequestDto.courseCode());

        assertEquals(new UserDto(feedbackSubmitter), feedbackResponseDto.instructor());
        assertEquals(new UserDto(student), feedbackResponseDto.student());
        assertEquals(userCourse.getCourse().getCode(), feedbackResponseDto.courseCode());
        assertEquals(feedbackRequestDto.feedback(), feedbackResponseDto.feedback());
    }

    private Response makeFeedbackSubmissionRequest(final User user, final FeedbackRequestDto feedbackRequestDto) {
        return getAuthTokenRequestSpec(user, requestSpecification)
                .body(feedbackRequestDto)
                .post(FEEDBACK_SUBMISSION_ENDPOINT);
    }

    private static FeedbackRequestDto getFeedbackDtoIgnoring(final GetMethodSelector<FeedbackRequestDto, ?> methodSelector) {
        return getModelIgnoringFields(FeedbackRequestDto.class, methodSelector);
    }
}
