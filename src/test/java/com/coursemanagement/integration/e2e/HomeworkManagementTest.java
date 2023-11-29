package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.FileRepository;
import com.coursemanagement.repository.HomeworkRepository;
import com.coursemanagement.repository.entity.FileEntity;
import com.coursemanagement.repository.entity.HomeworkEntity;
import com.coursemanagement.rest.dto.UploadHomeworkDto;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.HOMEWORK_DOWNLOAD_ENDPOINT;
import static com.coursemanagement.util.BaseEndpoints.HOMEWORK_UPLOAD_ENDPOINT;
import static com.coursemanagement.util.JwtTokenUtils.getTokenForUser;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.SECOND_STUDENT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql(value = "/scripts/add_homework.sql")
public class HomeworkManagementTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private FileRepository fileRepository;

    @Order(1)
    @TestFactory
    @DisplayName("Test homework uploading flow")
    Stream<DynamicTest> testHomeworkUploadingFlow() {
        final MultipartFile textFile = getMultipartFile("text/plain");
        final MultipartFile imageFile = getMultipartFile("image/jpeg");
        final UploadHomeworkDto uploadTextHomeworkDto = new UploadHomeworkDto(1L, textFile);

        return Stream.of(
                dynamicTest("Test homework uploading to not associated lesson",
                        () -> testUploadingUnauthorizedAccess(FIRST_STUDENT, new UploadHomeworkDto(10L, textFile))),
                dynamicTest("Test homework uploading to by another student",
                        () -> testUploadingUnauthorizedAccess(SECOND_STUDENT, uploadTextHomeworkDto)),
                dynamicTest("Test homework uploading without lesson id",
                        () -> testBadHomeworkUploadingRequest(new UploadHomeworkDto(null, textFile))),
                dynamicTest("Test valid homework uploading request with text file",
                        () -> testValidLessonUploadingRequest(uploadTextHomeworkDto)),
                dynamicTest("Test valid homework uploading request with image file",
                        () -> testValidLessonUploadingRequest(new UploadHomeworkDto(2L, imageFile)))
        );
    }

    @Order(2)
    @TestFactory
    @DisplayName("Test homework downloading flow")
    Stream<DynamicTest> testHomeworkDownloadingFlow() {
        final Long fileId = 3L;
        return Stream.of(
                dynamicTest("Test homework downloading by another student",
                        () -> testDownloadingUnauthorizedAccess(SECOND_STUDENT, fileId)),
                dynamicTest("Test homework downloading from not associated lesson",
                        () -> testDownloadingUnauthorizedAccess(FIRST_STUDENT, 4L)),
                dynamicTest("Test valid homework downloading",
                        () -> testValidLessonDownloadingRequest(fileId))
        );
    }

    private void testUploadingUnauthorizedAccess(final User user, final UploadHomeworkDto uploadHomeworkDto) {
        makeHomeworkUploadingRequest(user, uploadHomeworkDto)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testDownloadingUnauthorizedAccess(final User user, final Long fileId) {
        makeHomeworkDownloadingRequest(user, fileId)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testBadHomeworkUploadingRequest(final UploadHomeworkDto uploadHomeworkDto) {
        makeHomeworkUploadingRequest(FIRST_STUDENT, uploadHomeworkDto)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void testValidLessonUploadingRequest(final UploadHomeworkDto uploadHomeworkDto) throws Exception {
        makeHomeworkUploadingRequest(FIRST_STUDENT, uploadHomeworkDto)
                .then()
                .spec(validResponseSpecification);

        final HomeworkEntity savedHomeworkEntity = homeworkRepository.findAll().stream()
                .filter(homeworkEntity -> Objects.equals(homeworkEntity.getStudentId(), FIRST_STUDENT.getId())
                        && Objects.equals(homeworkEntity.getLessonId(), uploadHomeworkDto.lessonId()))
                .max(Comparator.comparing(HomeworkEntity::getId))
                .orElseGet(Assertions::fail);
        final FileEntity savedFileEntity = fileRepository.findById(savedHomeworkEntity.getFileId())
                .orElseGet(Assertions::fail);
        final MultipartFile fileToUpload = uploadHomeworkDto.file();

        assertEquals(uploadHomeworkDto.lessonId(), savedHomeworkEntity.getLessonId());
        assertNotNull(savedFileEntity);
        assertEquals(savedFileEntity.getFileName(), fileToUpload.getOriginalFilename());
        assertEquals(savedFileEntity.getFileType(), FileType.of(fileToUpload.getContentType()));
        assertArrayEquals(savedFileEntity.getFileContent(), fileToUpload.getBytes());
    }

    private void testValidLessonDownloadingRequest(final Long fileId) {
        final String fileContent = fileRepository.findById(fileId)
                .map(FileEntity::getFileContent)
                .map(String::new)
                .orElseThrow();
        makeHomeworkDownloadingRequest(FIRST_STUDENT, fileId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.BINARY)
                .body(equalTo(fileContent));
    }

    private Response makeHomeworkUploadingRequest(final User user, final UploadHomeworkDto uploadHomeworkDto) {
        final String jwt = getTokenForUser(user, requestSpecification);
        return given(requestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .contentType(ContentType.MULTIPART)
                .multiPart("lessonId", uploadHomeworkDto.lessonId())
                .multiPart(getMultipartSpec(uploadHomeworkDto.file()))
                .post(HOMEWORK_UPLOAD_ENDPOINT);
    }

    private Response makeHomeworkDownloadingRequest(final User user, final Long fileId) {
        final String jwt = getTokenForUser(user, requestSpecification);
        final String homeworkDownloadingEndpoint = HOMEWORK_DOWNLOAD_ENDPOINT + "/" + fileId;
        return given(requestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .get(homeworkDownloadingEndpoint);
    }

    private static MockMultipartFile getMultipartFile(final String filetype) {
        return new MockMultipartFile("file", "fileName", filetype, "Sample content".getBytes());
    }

    private static MultiPartSpecification getMultipartSpec(final MultipartFile multipartFile) {
        try {
            return new MultiPartSpecBuilder(multipartFile.getBytes())
                    .fileName(multipartFile.getOriginalFilename())
                    .mimeType(multipartFile.getContentType())
                    .build();
        } catch (final IOException e) {
            throw new SystemException("Cannot create multipart spec from file: " + multipartFile, SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
