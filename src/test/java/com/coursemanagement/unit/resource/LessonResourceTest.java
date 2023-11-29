package com.coursemanagement.unit.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.rest.LessonResource;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.LessonService;
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

import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.Constants.LESSONS_ENDPOINT;
import static com.coursemanagement.util.DateTimeUtils.formatDate;
import static com.coursemanagement.util.MvcUtils.makeMockMvcRequest;
import static com.coursemanagement.util.ResponseBodyMatcherUtils.responseBody;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredResourceTest(value = LessonResource.class)
class LessonResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LessonService lessonService;

    @Order(1)
    @TestFactory
    @DisplayName("Test instructor's assign mark endpoint")
    Stream<DynamicTest> testAssignMarkEndpoint() {
        final MarkAssigmentRequestDto requestDto = Instancio.create(MarkAssigmentRequestDto.class);
        final MarkAssignmentResponseDto responseDto = Instancio.of(MarkAssignmentResponseDto.class)
                .set(field(MarkAssignmentResponseDto::markSubmissionDate), formatDate(LocalDateTime.now()))
                .create();
        final String assignMarkEndpoint = LESSONS_ENDPOINT + "/assign-mark";
        return Stream.of(
                dynamicTest("Test empty body request",
                        () -> makeMockMvcRequest(mockMvc, POST, assignMarkEndpoint, INSTRUCTOR).andExpect(status().isBadRequest())),
                dynamicTest("Test unauthorized access to instructor's endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, POST, assignMarkEndpoint, requestDto, Role.INSTRUCTOR)),
                dynamicTest("Test valid mark assigment request",
                        () -> {
                            when(lessonService.assignMarkToUserLesson(requestDto)).thenReturn(responseDto);
                            makeMockMvcRequest(mockMvc, POST, assignMarkEndpoint, requestDto, INSTRUCTOR)
                                    .andExpect(responseBody().containsObjectAsJson(responseDto, MarkAssignmentResponseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                POST,
                                assignMarkEndpoint,
                                requestDto,
                                lessonService.assignMarkToUserLesson(requestDto)
                        )
                )
        );
    }
}