package com.coursemanagement.unit.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.model.File;
import com.coursemanagement.rest.HomeworkResource;
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

import java.util.List;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.BaseEndpoints.HOMEWORK_DOWNLOAD_ENDPOINT;
import static com.coursemanagement.util.BaseEndpoints.HOMEWORK_UPLOAD_ENDPOINT;
import static com.coursemanagement.util.MvcUtils.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredResourceTest(value = HomeworkResource.class)
class HomeworkResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HomeworkService homeworkService;
    @MockBean
    private LessonService lessonService;

    @Order(1)
    @TestFactory
    @DisplayName("Test uploading homework endpoint")
    Stream<DynamicTest> testUploadHomeworkEndpoint() {
        final MockMultipartFile multipartFile = new MockMultipartFile("file", "sample.txt", "text/plain", "Sample content" .getBytes());
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> mockMvc.perform(multipart(HOMEWORK_UPLOAD_ENDPOINT).file(multipartFile).param("lessonId", "1"))
                                .andExpect(status().isUnauthorized())
                ),
                dynamicTest("Test valid homework uploading request with text file",
                        () -> {
                            doNothing().when(homeworkService).uploadHomework(anyLong(), anyLong(), any());
                            mockMvc.perform(multipart(HOMEWORK_UPLOAD_ENDPOINT)
                                            .file(multipartFile)
                                            .param("lessonId", "1")
                                            .with(jwt().jwt(j -> j.subject(FIRST_STUDENT.getEmail()))
                                                    .authorities(List.of((GrantedAuthority) () -> "ROLE_STUDENT"))))
                                    .andExpect(status().isOk());
                        }
                )
        );
    }

    @Order(2)
    @TestFactory
    @DisplayName("Test downloading homework endpoint")
    Stream<DynamicTest> testDownloadHomeworkEndpoint() {
        final long fileId = 334L;
        final String downloadHomeworkEndpoint = HOMEWORK_DOWNLOAD_ENDPOINT + "/" + fileId;
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
