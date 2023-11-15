package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.FileRepository;
import com.coursemanagement.repository.entity.FileEntity;
import com.coursemanagement.repository.entity.HomeworkEntity;
import com.coursemanagement.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

import static com.coursemanagement.util.JwtTokenUtils.getTokenForUser;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.MULTIPART;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.verify;

@IntegrationTest
@Sql("/scripts/add_lessons_with_marks.sql")
public class HomeworkUploadingTest {
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private RequestSpecification requestSpecification;
    private RequestSpecification fileRequestSpecification;

    @BeforeEach
    void setUp() {
        fileRequestSpecification = new RequestSpecBuilder()
                .addRequestSpecification(requestSpecification)
                .setContentType(MULTIPART)
                .build();
    }

    @TestFactory
    @DisplayName("Test course enrollment flow")
    Stream<DynamicTest> testCourseEnrollmentFlow() throws IOException {
        final MultipartFile textFile = createMultipartFile("text/plain");
        final MultipartFile imageFile = createMultipartFile("image/jpeg");
        final long lessonId = 22324;
        final String endpoint = "/api/v1/lessons/" + lessonId + "/homework/upload";

        final String firstStudentJwt = getTokenForUser(FIRST_STUDENT, requestSpecification);
        final String secondStudentJwt = getTokenForUser(SECOND_STUDENT, requestSpecification);
        final String instructorJwt = getTokenForUser(INSTRUCTOR, requestSpecification);


        return Stream.of(
                dynamicTest("Test homework uploading to not associated lesson", () -> testUnauthorizedAccess(firstStudentJwt, textFile)),
                dynamicTest("Test homework uploading to by another student", () -> testUnauthorizedAccess(secondStudentJwt, textFile)),

                dynamicTest("Test first course enrollment", () -> testValidLessonUploadingRequest(FIRST_STUDENT, lessonId, textFile)
//                dynamicTest("Test enroll one more course to the end of the Limit", () -> testValidCourseEnrollmentRequest(secondRequest, firstStudentJwt)),
//                dynamicTest("Test enroll already taken courses", () -> testValidCourseEnrollmentRequest(firstRequest, firstStudentJwt)),
//                dynamicTest("Test out of limit course enrollment", () -> testBadCourseEnrollmentRequest(outOfLimitRequest, firstStudentJwt))
                ));
    }

    private void testUnauthorizedAccess(final String jwt, final MultipartFile multipartFile) throws Exception {
        given(fileRequestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .multiPart(getMultipartSpec(multipartFile))
                .post("/api/v1/lessons/22324/homework/upload")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void testValidLessonUploadingRequest(final User user, final Long lessonId, final MultipartFile file) {
        final String jwt = getTokenForUser(user, requestSpecification);

        given(requestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .multiPart("file", file)
                .contentType("multipart/form-data")
                .pathParam("lesson-id", lessonId)
                .post("/api/v1/lessons/22324/homework/upload")
                .then()
                .spec(validResponseSpecification);

        final ArgumentCaptor<FileEntity> fileArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        final ArgumentCaptor<HomeworkEntity> homeworkEntityArgumentCaptor = ArgumentCaptor.forClass(HomeworkEntity.class);
        verify(fileRepository).save(fileArgumentCaptor.capture());

        System.out.println(fileArgumentCaptor.getValue().getId());
    }

//    private void testBadCourseEnrollmentRequest(final StudentEnrollInCourseRequestDto requestDto, final String jwt) {
//        given(requestSpecification)
//                .header("Authorization", "Bearer " + jwt)
//                .body(requestDto)
//                .post(COURSE_ENROLLMENT_ENDPOINT)
//                .then()
//                .statusCode(HttpStatus.BAD_REQUEST.value());
//    }

    private static MockMultipartFile createMultipartFile(final String filetype) {
        return new MockMultipartFile("file", "originalFileName", filetype, "Sample content".getBytes());
    }

    private static MultiPartSpecification getMultipartSpec(final MultipartFile multipartFile) throws IOException {
        return new MultiPartSpecBuilder(multipartFile.getBytes())
                .fileName(multipartFile.getOriginalFilename())
                .mimeType(multipartFile.getContentType())
                .build();
    }
}
