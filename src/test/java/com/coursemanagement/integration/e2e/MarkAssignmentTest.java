package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.LessonMarkRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.entity.LessonMarkEntity;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.MarkAssignmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserDto;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.MARK_ASSIGNMENT_ENDPOINT;
import static com.coursemanagement.util.JwtTokenUtils.getAuthTokenRequestSpec;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql(value = "/scripts/add_lessons.sql")
public class MarkAssignmentTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonMarkRepository lessonMarkRepository;

    @TestFactory
    @DisplayName("Test invalid mark assignment flow")
    Stream<DynamicTest> testBadMarkAssignmentFlow() {
        return Stream.of(
                dynamicTest("Test unauthorized mark assignment new user access",
                        () -> testUnauthorizedMarkAssignment(NEW_USER, Instancio.create(MarkAssignmentRequestDto.class))),
                dynamicTest("Test unauthorized mark assignment student access",
                        () -> testUnauthorizedMarkAssignment(FIRST_STUDENT, Instancio.create(MarkAssignmentRequestDto.class))),
                dynamicTest("Test unauthorized mark assignment when student is not associated with lesson",
                        () -> testUnauthorizedMarkAssignment(INSTRUCTOR, new MarkAssignmentRequestDto(2L, 3L, 25L, Mark.EXCELLENT))),
                dynamicTest("Test mark assignment without specifying an instructor",
                        () -> testBadMarkAssignmentRequest(getMarkAssignmentDtoIgnoring(MarkAssignmentRequestDto::instructorId))),
                dynamicTest("Test mark assignment without specifying a student",
                        () -> testBadMarkAssignmentRequest(getMarkAssignmentDtoIgnoring(MarkAssignmentRequestDto::studentId))),
                dynamicTest("Test mark assignment without specifying a lesson",
                        () -> testBadMarkAssignmentRequest(getMarkAssignmentDtoIgnoring(MarkAssignmentRequestDto::lessonId))),
                dynamicTest("Test mark assignment without specifying mark",
                        () -> testBadMarkAssignmentRequest(getMarkAssignmentDtoIgnoring(MarkAssignmentRequestDto::mark)))
        );
    }

    @ParameterizedTest(name = "[{index}] Test valid mark assignment ({2}) by {1}")
    @MethodSource(value = "userMarkProvider")
    @DisplayName("Test valid mark assignment request")
    void testValidMarkAssignmentRequest(final User user, final String ignore, final Mark mark) {
        final var graderId = user.getId();
        final var studentId = FIRST_STUDENT.getId();
        final var markAssigmentRequestDto = new MarkAssignmentRequestDto(graderId, studentId, 1L, mark);

        final MarkAssignmentResponseDto actualResponse = makeMarkAssignmentRequest(user, markAssigmentRequestDto)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/markAssignmentResponseSchema.json"))
                .extract()
                .as(MarkAssignmentResponseDto.class);

        final UserDto grader = new UserDto(getUserById(graderId));
        final UserDto student = new UserDto(getUserById(studentId));
        final LessonDto lessonDto = lessonRepository.findById(markAssigmentRequestDto.lessonId()).map(LessonDto::new).orElseThrow();
        final LocalDateTime markSubmissionDate = lessonMarkRepository.findAllByStudentIdAndLessonCourseCode(studentId, 22324L).stream()
                .findFirst()
                .map(LessonMarkEntity::getMarkSubmissionDate)
                .orElseThrow();

        final MarkAssignmentResponseDto expectedResponse = new MarkAssignmentResponseDto(student, lessonDto, grader, mark, markSubmissionDate);
        assertEquals(expectedResponse, actualResponse);
    }

    private static Stream<Arguments> userMarkProvider() {
        return Arrays.stream(Mark.values())
                .map(mark -> List.of(
                        Arguments.of(INSTRUCTOR, "instructor", mark),
                        Arguments.of(ADMIN, "admin", mark)
                ))
                .flatMap(Collection::stream);
    }

    private void testUnauthorizedMarkAssignment(final User user, final MarkAssignmentRequestDto markAssignmentRequestDto) {
        makeMarkAssignmentRequest(user, markAssignmentRequestDto)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testBadMarkAssignmentRequest(final MarkAssignmentRequestDto markAssignmentRequestDto) {
        makeMarkAssignmentRequest(FIRST_STUDENT, markAssignmentRequestDto)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private Response makeMarkAssignmentRequest(final User user, final MarkAssignmentRequestDto markAssignmentRequestDto) {
        return getAuthTokenRequestSpec(user, requestSpecification)
                .body(markAssignmentRequestDto)
                .post(MARK_ASSIGNMENT_ENDPOINT);
    }

    private static MarkAssignmentRequestDto getMarkAssignmentDtoIgnoring(final GetMethodSelector<MarkAssignmentRequestDto, ?> methodSelector) {
        return getModelIgnoringFields(MarkAssignmentRequestDto.class, methodSelector);
    }
}
