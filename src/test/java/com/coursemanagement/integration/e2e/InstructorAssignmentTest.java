package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.InstructorAssignmentRequestDto;
import com.coursemanagement.rest.dto.InstructorAssignmentResponseDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.UserCourseService;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.COURSE_ASSIGNMENT_ENDPOINT;
import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;
import static com.coursemanagement.util.JwtTokenUtils.getTokenForUser;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql("/scripts/add_courses.sql")
public class InstructorAssignmentTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserCourseService userCourseService;

    @TestFactory
    @DisplayName("Test instructor assignment flow")
    Stream<DynamicTest> testInstructorAssignmentFlow() {
        final Long existingCourseCode = 22324L;
        final Long nonExistingCourseCode = 3L;
        final String adminsJwt = getTokenForUser(ADMIN, requestSpecification);
        requestSpecification.header("Authorization", "Bearer " + adminsJwt);

        return Stream.of(
                dynamicTest("Test potential instructor doesn't have respective role",
                        () -> testBadRequestInstructorAssignment(getCourseAssignment(FIRST_STUDENT.getId(), existingCourseCode))),
                dynamicTest("Test assign instructor to non-existing course",
                        () -> testBadRequestInstructorAssignment(getCourseAssignment(INSTRUCTOR.getId(), nonExistingCourseCode))),
                dynamicTest("Test valid instructor assignment request",
                        () -> testValidRequestInstructorAssignment(getCourseAssignment(INSTRUCTOR.getId(), existingCourseCode))),
                dynamicTest("Test assign instructor to already assigned course",
                        () -> testValidRequestInstructorAssignment(getCourseAssignment(INSTRUCTOR.getId(), existingCourseCode)))
        );
    }

    private void testBadRequestInstructorAssignment(final InstructorAssignmentRequestDto requestDto) {
        given(requestSpecification)
                .body(requestDto)
                .post(COURSE_ASSIGNMENT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void testValidRequestInstructorAssignment(final InstructorAssignmentRequestDto requestDto) {
        final InstructorAssignmentResponseDto responseDto = given(requestSpecification)
                .body(requestDto)
                .post(COURSE_ASSIGNMENT_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/courseAssignmentResponseSchema.json"))
                .extract()
                .as(InstructorAssignmentResponseDto.class);

        final Course courseByCode = courseService.getCourseByCode(requestDto.courseCode());
        final User userFromCourse = courseByCode.getUsers().stream()
                .filter(user -> Objects.equals(user.getId(), requestDto.instructorId()))
                .findFirst()
                .orElseGet(Assertions::fail);
        assertNotNull(userFromCourse);
        assertTrue(userFromCourse.getRoles().contains(Role.INSTRUCTOR));
        assertTrue(responseDto.instructors().stream()
                .anyMatch(userDto -> Objects.equals(userDto.id(), requestDto.instructorId())));

        final List<UserCourse> instructorsCourses = userCourseService.getUserCoursesByUserId(requestDto.instructorId()).stream()
                .filter(course -> Objects.equals(course.getCourse().getCode(), requestDto.courseCode()))
                .toList();
        assertEquals(1, instructorsCourses.size());

        final UserCourse userCourse = instructorsCourses.get(0);
        assertEquals(UserCourseStatus.STARTED, userCourse.getStatus());
        assertThat(userCourse.getEnrollmentDate().isBefore(LocalDateTime.now(DEFAULT_ZONE_ID))).isTrue();
        assertNull(userCourse.getAccomplishmentDate());
    }

    private InstructorAssignmentRequestDto getCourseAssignment(final Long userId, final Long courseCode) {
        return new InstructorAssignmentRequestDto(userId, courseCode);
    }
}