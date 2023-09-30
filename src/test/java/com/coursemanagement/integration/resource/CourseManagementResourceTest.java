package com.coursemanagement.integration.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.rest.CourseManagementResource;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.service.CourseManagementService;
import com.coursemanagement.service.FeedbackService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.coursemanagement.config.ResponseBodyMatchers.responseBody;
import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.Constants.COURSE_MANAGEMENT_ENDPOINT;
import static com.coursemanagement.util.DateTimeUtils.formatDate;
import static com.coursemanagement.util.MvcUtil.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.*;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredResourceTest(value = CourseManagementResource.class)
class CourseManagementResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseManagementService courseManagementService;
    @MockBean
    private FeedbackService feedbackService;

    @Order(1)
    @TestFactory
    @DisplayName("Test admins assign instructor to course endpoint")
    Stream<DynamicTest> testAssignInstructorToCourseEndpoint() {
        final String assignInstructorEndpoint = COURSE_MANAGEMENT_ENDPOINT + "/assign-instructor";
        final CourseAssignmentRequestDto requestDto = Instancio.create(CourseAssignmentRequestDto.class);
        final CourseAssignmentResponseDto responseDto = Instancio.create(CourseAssignmentResponseDto.class);
        return Stream.of(
                dynamicTest("Test empty body request",
                        () -> makeMockMvcRequest(mockMvc, POST, assignInstructorEndpoint, ADMIN).andExpect(status().isBadRequest())),
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, POST, assignInstructorEndpoint, requestDto, Role.ADMIN)),
                dynamicTest("Test valid course assigment request",
                        () -> {
                            when(courseManagementService.assignInstructorToCourse(requestDto)).thenReturn(responseDto);
                            makeMockMvcRequest(mockMvc, POST, assignInstructorEndpoint, requestDto, ADMIN)
                                    .andExpect(responseBody().containsObjectAsJson(responseDto, CourseAssignmentResponseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                POST,
                                assignInstructorEndpoint,
                                requestDto,
                                courseManagementService.assignInstructorToCourse(requestDto)
                        )
                )
        );
    }

    @Order(2)
    @TestFactory
    @DisplayName("Test course enrollments endpoint")
    Stream<DynamicTest> testCourseEnrollmentsEndpoint() {
        final String courseEnrollmentsEndpoint = COURSE_MANAGEMENT_ENDPOINT + "/enrollments";
        final StudentEnrollInCourseRequestDto requestDto = Instancio.create(StudentEnrollInCourseRequestDto.class);
        final StudentEnrollInCourseResponseDto responseDto = Instancio.create(StudentEnrollInCourseResponseDto.class);
        return Stream.of(
                dynamicTest("Test empty body request",
                        () -> makeMockMvcRequest(mockMvc, POST, courseEnrollmentsEndpoint, FIRST_STUDENT).andExpect(status().isBadRequest())),
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, POST, courseEnrollmentsEndpoint, requestDto)),
                dynamicTest("Test valid course enrollment request",
                        () -> {
                            when(courseManagementService.enrollStudentInCourses(requestDto)).thenReturn(responseDto);
                            makeMockMvcRequest(mockMvc, POST, courseEnrollmentsEndpoint, requestDto, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectAsJson(responseDto, StudentEnrollInCourseResponseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                POST,
                                courseEnrollmentsEndpoint,
                                requestDto,
                                courseManagementService.enrollStudentInCourses(requestDto)
                        )
                )
        );
    }

    @Order(3)
    @TestFactory
    @DisplayName("Test feedback submission endpoint")
    Stream<DynamicTest> testFeedbackSubmissionEndpoint() {
        final String feedbackSubmissionEndpoint = COURSE_MANAGEMENT_ENDPOINT + "/provide-feedback";
        final FeedbackRequestDto requestDto = Instancio.create(FeedbackRequestDto.class);
        final FeedbackResponseDto responseDto = Instancio.of(FeedbackResponseDto.class)
                .set(field(FeedbackResponseDto::feedbackSubmissionDate), formatDate(LocalDateTime.now()))
                .create();
        return Stream.of(
                dynamicTest("Test empty body request",
                        () -> makeMockMvcRequest(mockMvc, POST, feedbackSubmissionEndpoint, INSTRUCTOR).andExpect(status().isBadRequest())),
                dynamicTest("Test unauthorized access to instructors endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, POST, feedbackSubmissionEndpoint, requestDto, Role.INSTRUCTOR)),
                dynamicTest("Test valid feedback submission request",
                        () -> {
                            when(feedbackService.provideFeedbackToUserCourse(requestDto)).thenReturn(responseDto);
                            makeMockMvcRequest(mockMvc, POST, feedbackSubmissionEndpoint, requestDto, INSTRUCTOR)
                                    .andExpect(responseBody().containsObjectAsJson(responseDto, FeedbackResponseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                POST,
                                feedbackSubmissionEndpoint,
                                requestDto,
                                feedbackService.provideFeedbackToUserCourse(requestDto)
                        )
                )
        );
    }

    @Order(4)
    @TestFactory
    @DisplayName("Test course completion endpoint")
    Stream<DynamicTest> testCourseCompletionEndpoint() {
        final String courseCompletionEndpoint = COURSE_MANAGEMENT_ENDPOINT + "/complete";
        final CourseCompletionRequestDto requestDto = Instancio.create(CourseCompletionRequestDto.class);
        final UserCourseDetailsDto responseDto = Instancio.create(UserCourseDetailsDto.class);
        return Stream.of(
                dynamicTest("Test empty body request",
                        () -> makeMockMvcRequest(mockMvc, POST, courseCompletionEndpoint, FIRST_STUDENT).andExpect(status().isBadRequest())),
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, POST, courseCompletionEndpoint, requestDto)),
                dynamicTest("Test valid course completion request",
                        () -> {
                            when(courseManagementService.completeStudentCourse(requestDto)).thenReturn(responseDto);
                            makeMockMvcRequest(mockMvc, POST, courseCompletionEndpoint, requestDto, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectAsJson(responseDto, UserCourseDetailsDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                POST,
                                courseCompletionEndpoint,
                                requestDto,
                                courseManagementService.completeStudentCourse(requestDto)
                        )
                )
        );
    }
}