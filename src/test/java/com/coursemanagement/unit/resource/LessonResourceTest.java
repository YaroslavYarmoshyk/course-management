package com.coursemanagement.unit.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.File;
import com.coursemanagement.rest.LessonResource;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.HomeworkService;
import com.coursemanagement.service.LessonService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.coursemanagement.util.ResponseBodyMatcherUtils.responseBody;
import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.Constants.LESSONS_ENDPOINT;
import static com.coursemanagement.util.DateTimeUtils.formatDate;
import static com.coursemanagement.util.MvcUtils.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredResourceTest(value = LessonResource.class)
class LessonResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LessonService lessonService;
    @MockBean
    private HomeworkService homeworkService;

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

    @Order(2)
    @TestFactory
    @DisplayName("Test uploading homework endpoint")
    Stream<DynamicTest> testUploadHomeworkEndpoint() {
        final long lessonId = 223L;
        final String uploadHomeworkEndpoint = LESSONS_ENDPOINT + "/" + lessonId + "/homework/upload";
        final MockMultipartFile multipartFile = new MockMultipartFile("file", "sample.txt", "text/plain", "Sample content".getBytes());
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> mockMvc.perform(multipart(uploadHomeworkEndpoint).file(multipartFile))
                                .andExpect(status().isUnauthorized())
                ),
                dynamicTest("Test valid homework uploading request",
                        () -> {
                            doNothing().when(homeworkService).uploadHomework(anyLong(), anyLong(), any());
                            mockMvc.perform(multipart(uploadHomeworkEndpoint)
                                            .file(multipartFile)
                                            .param("studentId", FIRST_STUDENT.getId().toString())
                                            .with(jwt().jwt(j -> j.subject(FIRST_STUDENT.getEmail()))
                                                    .authorities(List.of((GrantedAuthority) () -> "ROLE_STUDENT"))))
                                    .andExpect(status().isOk());
                            verify(homeworkService).uploadHomework(FIRST_STUDENT.getId(), lessonId, multipartFile);
                        })
        );
    }

    @Order(3)
    @TestFactory
    @DisplayName("Test downloading homework endpoint")
    Stream<DynamicTest> testDownloadHomeworkEndpoint() {
        final long fileId = 334L;
        final String downloadHomeworkEndpoint = LESSONS_ENDPOINT + "/homework/download/" + fileId;
        final File file = Instancio.create(File.class);
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, downloadHomeworkEndpoint)),
                dynamicTest("Test valid homework downloading request",
                        () -> {
                            when(homeworkService.downloadHomework(FIRST_STUDENT.getId(), fileId)).thenReturn(file);
                            makeMockMvcRequest(mockMvc, GET, downloadHomeworkEndpoint, FIRST_STUDENT)
                                    .andExpect(status().isOk())
                                    .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                                    .andExpect(content().bytes(file.getFileContent()));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                downloadHomeworkEndpoint,
                                homeworkService.downloadHomework(anyLong(), anyLong())
                        )
                )
        );
    }

}