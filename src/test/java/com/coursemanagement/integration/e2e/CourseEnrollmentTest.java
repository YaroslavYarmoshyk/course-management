package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.service.UserCourseService;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.COURSE_ENROLLMENT_ENDPOINT;
import static com.coursemanagement.util.JwtTokenUtils.getTokenForUser;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql("/scripts/add_courses.sql")
public class CourseEnrollmentTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private UserCourseService userCourseService;


    @TestFactory
    @DisplayName("Test course enrollment flow")
    Stream<DynamicTest> testCourseEnrollmentFlow() {
        final Long userId = FIRST_STUDENT.getId();
        final String firstStudentJwt = getTokenForUser(FIRST_STUDENT, requestSpecification);
        final var firstRequest = new StudentEnrollInCourseRequestDto(userId, Set.of(22324L, 34432L, 99831L, 56548L));
        final var secondRequest = new StudentEnrollInCourseRequestDto(userId, Set.of(34568L));
        final var outOfLimitRequest = new StudentEnrollInCourseRequestDto(userId, Set.of(67891L));

        return Stream.of(
                dynamicTest("Test unauthorized student enrollment access", () -> testUnauthorizedAccess(firstRequest)),
                dynamicTest("Test first course enrollment", () -> testValidCourseEnrollmentRequest(firstRequest, firstStudentJwt)),
                dynamicTest("Test enroll one more course to the end of the Limit", () -> testValidCourseEnrollmentRequest(secondRequest, firstStudentJwt)),
                dynamicTest("Test enroll already taken courses", () -> testValidCourseEnrollmentRequest(firstRequest, firstStudentJwt)),
                dynamicTest("Test out of limit course enrollment", () -> testBadCourseEnrollmentRequest(outOfLimitRequest, firstStudentJwt))
        );
    }

    private void testUnauthorizedAccess(final StudentEnrollInCourseRequestDto requestDto) {
        final String anotherStudentJwt = getTokenForUser(SECOND_STUDENT, requestSpecification);
        final String instructorJwt = getTokenForUser(INSTRUCTOR, requestSpecification);

        given(requestSpecification)
                .header("Authorization", "Bearer " + anotherStudentJwt)
                .body(requestDto)
                .post(COURSE_ENROLLMENT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        given(requestSpecification)
                .header("Authorization", "Bearer " + instructorJwt)
                .body(requestDto)
                .post(COURSE_ENROLLMENT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testValidCourseEnrollmentRequest(final StudentEnrollInCourseRequestDto requestDto, final String jwt) {
        final Long userId = requestDto.studentId();

        final ValidatableResponse response = given(requestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .body(requestDto)
                .post(COURSE_ENROLLMENT_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/courseEnrollmentResponseSchema.json"));

        final Set<UserCourse> userCoursesAfterEnrollment = userCourseService.getUserCoursesByUserId(userId);
        final Set<Long> takenCourseCodes = userCoursesAfterEnrollment.stream()
                .map(userCourse -> userCourse.getCourse().getCode())
                .collect(Collectors.toSet());
        final StudentEnrollInCourseResponseDto responseDto = response.extract().as(StudentEnrollInCourseResponseDto.class);
        final Set<Long> responseCourseCodes = responseDto.studentCourses().stream()
                .map(UserCourseDto::code)
                .collect(Collectors.toSet());

        assertFalse(userCoursesAfterEnrollment.isEmpty());
        assertTrue(takenCourseCodes.containsAll(requestDto.courseCodes()));
        assertEquals(responseDto.studentId(), userId);
        assertTrue(CollectionUtils.isEqualCollection(responseCourseCodes, takenCourseCodes));
    }

    private void testBadCourseEnrollmentRequest(final StudentEnrollInCourseRequestDto requestDto, final String jwt) {
        given(requestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .body(requestDto)
                .post(COURSE_ENROLLMENT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
