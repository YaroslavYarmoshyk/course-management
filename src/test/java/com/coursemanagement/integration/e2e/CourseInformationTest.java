package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserInfoDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.UserService;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.coursemanagement.util.Constants.COURSES_ENDPOINT;
import static com.coursemanagement.util.JwtTokenUtils.getAuthTokenRequestSpec;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql(value = "/scripts/add_lessons_with_marks.sql")
public class CourseInformationTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private UserService userService;

    @TestFactory
    @DisplayName("Test get course information")
    Stream<DynamicTest> testGettingCourseInformation() {
        final Long instructorsCourseCode = 56548L;
        return Stream.of(
                dynamicTest("Test instructor courses", () -> testCourseRequest(INSTRUCTOR, getInstructorCourses())),
                dynamicTest("Test student courses", () -> testCourseRequest(FIRST_STUDENT, getFirstStudentCourses())),
                dynamicTest("Test get students per course unauthorized request", () -> testStudentsPerCourseUnauthorizedRequest(instructorsCourseCode)),
                dynamicTest("Test get students per course valid request", () -> testStudentsPerCourseValidRequest(instructorsCourseCode, getPhysicsCourseStudents())),
                dynamicTest("Test getting student lesson information with content", () -> testStudentLessonsRequest(getFistStudentLessons()))
        );
    }

    private void testCourseRequest(final User user, final Set<UserCourseDto> expectedCourses) {
        final List<UserCourseDto> actualCourses = Arrays.stream(getCourseRequest(user)
                .then()
                .spec(validResponseSpecification)
                .body(matchesJsonSchemaInClasspath("schemas/courseInformationResponseSchema.json"))
                .extract()
                .as(UserCourseDto[].class)).toList();

        assertTrue(CollectionUtils.isEqualCollection(expectedCourses, actualCourses));
    }

    private void testStudentsPerCourseUnauthorizedRequest(final Long courseCode) {
        getStudentsPerCourseRequest(FIRST_STUDENT, courseCode)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testStudentsPerCourseValidRequest(final Long courseCode, final Set<UserInfoDto> expectedUsers) {
        final List<UserInfoDto> actualUsers = Arrays.stream(getStudentsPerCourseRequest(INSTRUCTOR, courseCode)
                .then()
                .spec(validResponseSpecification)
                .body(matchesJsonSchemaInClasspath("schemas/studentsPerCourseResponseSchema.json"))
                .extract()
                .as(UserInfoDto[].class)).toList();

        assertTrue(CollectionUtils.isEqualCollection(expectedUsers, actualUsers));
    }

    private void testStudentLessonsRequest(final Set<UserLessonDto> expectedLessons) {
        final List<UserLessonDto> actualUserLessons = Arrays.stream(getAuthTokenRequestSpec(FIRST_STUDENT, requestSpecification)
                .get(String.format("%s%s%s%s", COURSES_ENDPOINT, "/", 22324, "/lessons"))
                .then()
                .spec(validResponseSpecification)
                .body(matchesJsonSchemaInClasspath("schemas/studentLessonResponseSchema.json"))
                .extract()
                .as(UserLessonDto[].class)).toList();

        assertTrue(CollectionUtils.isEqualCollection(expectedLessons, actualUserLessons));
    }

    private Response getCourseRequest(final User user) {
        return getAuthTokenRequestSpec(user, requestSpecification)
                .get(COURSES_ENDPOINT);
    }

    private Response getStudentsPerCourseRequest(final User user, final Long courseCode) {
        return getAuthTokenRequestSpec(user, requestSpecification)
                .get(String.format("%s%s%s%s", COURSES_ENDPOINT, "/", courseCode, "/students"));
    }

    private static Set<UserCourseDto> getInstructorCourses() {
        return Set.of(
                new UserCourseDto(76552L, "Computer Science", "Introduction to computer programming", UserCourseStatus.STARTED),
                new UserCourseDto(22324L, "Mathematics", "Introductory course on mathematics", UserCourseStatus.STARTED),
                new UserCourseDto(56548L, "Physics", "Fundamentals of physics", UserCourseStatus.STARTED)
        );
    }

    private static Set<UserCourseDto> getFirstStudentCourses() {
        return Set.of(
                new UserCourseDto(34432L, "History", "Overview of world history", UserCourseStatus.STARTED),
                new UserCourseDto(56548L, "Physics", "Fundamentals of physics", UserCourseStatus.STARTED),
                new UserCourseDto(22324L, "Mathematics", "Introductory course on mathematics", UserCourseStatus.STARTED)
        );
    }

    private Set<UserInfoDto> getPhysicsCourseStudents() {
        return Set.of(new UserInfoDto(userService.getUserById(FIRST_STUDENT.getId())));
    }

}
