package com.coursemanagement.unit.service;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.File;
import com.coursemanagement.repository.HomeworkRepository;
import com.coursemanagement.repository.entity.HomeworkEntity;
import com.coursemanagement.service.FileService;
import com.coursemanagement.service.UserAssociationService;
import com.coursemanagement.service.impl.HomeworkServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class HomeworkServiceImplTest {
    @InjectMocks
    private HomeworkServiceImpl homeworkService;
    @Mock
    private HomeworkRepository homeworkRepository;
    @Mock
    private UserAssociationService userAssociationService;
    @Mock
    private FileService fileService;
    @Captor
    private ArgumentCaptor<HomeworkEntity> argumentCaptor;

    @Order(1)
    @Test
    @DisplayName("Test homework file uploading")
    void tesHomeworkUploading() throws IOException {
        final Long studentId = FIRST_STUDENT.getId();
        final Long lessonId = 1000L;
        final MultipartFile multipartFile = new MockMultipartFile("testFileName", InputStream.nullInputStream());
        final File file = Instancio.create(File.class);

        when(userAssociationService.isUserAssociatedWithLesson(studentId, lessonId)).thenReturn(true);
        when(fileService.createFile(multipartFile)).thenReturn(file);

        homeworkService.uploadHomework(studentId, lessonId, multipartFile);

        verify(homeworkRepository, atLeastOnce()).save(argumentCaptor.capture());
        final HomeworkEntity homeworkEntity = argumentCaptor.getValue();
        assertEquals(studentId, homeworkEntity.getStudentId());
        assertEquals(lessonId, homeworkEntity.getLessonId());
        assertEquals(file.getId(), homeworkEntity.getFileId());
    }

    @Order(2)
    @Test
    @DisplayName("Test homework file downloading")
    void testHomeworkDownloading() {
        final File file = Instancio.create(File.class);
        final Long studentId = FIRST_STUDENT.getId();
        final Long fileId = file.getId();

        when(userAssociationService.isUserAssociatedWithLessonFile(studentId, fileId)).thenReturn(true);

        homeworkService.downloadHomework(studentId, fileId);

        verify(fileService).getFileById(fileId);

    }

    @Order(3)
    @TestFactory
    @DisplayName("Test homework file validation")
    Stream<DynamicTest> testHomeworkManagement() {
        return Stream.of(
                dynamicTest("Test user is not associated with lesson during uploading", this::testHomeworkUploadingValidation),
                dynamicTest("Test user is not associated with lesson file during downloading", this::testHomeworkDownloadingValidation));

    }

    void testHomeworkUploadingValidation() {
        final Long studentId = FIRST_STUDENT.getId();
        final Long lessonId = 12L;

        when(userAssociationService.isUserAssociatedWithLesson(studentId, lessonId)).thenReturn(false);

        assertThrowsWithMessage(
                () -> homeworkService.uploadHomework(studentId, lessonId, new MockMultipartFile("Test file name", InputStream.nullInputStream())),
                SystemException.class,
                "Access to the homework uploading is limited to associated lesson only"
        );
    }

    void testHomeworkDownloadingValidation() {
        final Long studentId = FIRST_STUDENT.getId();
        final Long fileId = 23L;

        when(userAssociationService.isUserAssociatedWithLessonFile(studentId, fileId)).thenReturn(false);

        assertThrowsWithMessage(
                () -> homeworkService.downloadHomework(studentId, fileId),
                SystemException.class,
                "Access to the homework downloading is limited to associated lesson only"
        );
    }
}