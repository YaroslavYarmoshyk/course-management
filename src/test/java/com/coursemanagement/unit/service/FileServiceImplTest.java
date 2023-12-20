package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.File;
import com.coursemanagement.repository.FileRepository;
import com.coursemanagement.repository.entity.FileEntity;
import com.coursemanagement.service.impl.FileServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class FileServiceImplTest {
    @InjectMocks
    private FileServiceImpl fileService;
    @Mock
    private FileRepository fileRepository;
    @Spy
    private ModelMapper mapper;
    private static final String FULL_TEST_FILE_NAME = "Test file name.txt";
    private static final String FILE_NAME_EXTENSION = FULL_TEST_FILE_NAME.substring(FULL_TEST_FILE_NAME.indexOf("."));
    private static final String TEST_FILE_NAME = FULL_TEST_FILE_NAME.replace(FILE_NAME_EXTENSION, "");

    @Order(1)
    @Test
    @DisplayName("Test get file by id")
    void testGetFileById() {
        final FileEntity fileEntity = Instancio.create(FileEntity.class);
        final Long fileId = fileEntity.getId();
        final long nonExistingFileId = fileId + 1;

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileEntity));

        final File file = fileService.getFileById(fileId);
        assertEquals(fileId, file.getId());
        assertEquals(fileEntity.getFileName(), file.getFileName());
        assertEquals(fileEntity.getFileType(), file.getFileType());
        assertEquals(fileEntity.getFileContent().length, file.getFileContent().length);
        assertThrowsWithMessage(
                () -> fileService.getFileById(nonExistingFileId),
                SystemException.class,
                "File with id: " + nonExistingFileId + " not found"
        );
    }

    @Order(2)
    @Test
    @DisplayName("Test file saving")
    void testFileSaving() {
        final File file = Instancio.create(File.class);
        final FileEntity fileEntity = mapper.map(file, FileEntity.class);

        when(fileRepository.save(argThat(
                entity -> {
                    assertEquals(file.getId(), entity.getId());
                    assertEquals(file.getFileName(), entity.getFileName());
                    assertEquals(file.getFileType(), entity.getFileType());
                    assertEquals(file.getFileContent().length, entity.getFileContent().length);
                    return true;
                }
        ))).thenReturn(fileEntity);

        final File savedFile = fileService.save(file);
        assertEquals(file, savedFile);
    }


    @Order(3)
    @Test
    @DisplayName("Test file creation")
    void testFileCreation() throws IOException {
        final MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, FULL_TEST_FILE_NAME, "text/plain", new byte[0]);
        final FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(multipartFile.getName());
        fileEntity.setFileType(FileType.of(multipartFile.getContentType()));
        fileEntity.setFileContent(multipartFile.getBytes());

        when(fileRepository.save(argThat(entity -> {
            assertEquals(FileType.of(multipartFile.getContentType()), entity.getFileType());
            assertEquals(TEST_FILE_NAME, entity.getFileName());
            assertEquals(0, entity.getFileContent().length);
            return true;
        }))).thenReturn(fileEntity);

        final File savedFile = fileService.createFile(multipartFile);

        assertEquals(multipartFile.getName(), savedFile.getFileName());
        assertEquals(FileType.of(multipartFile.getContentType()), savedFile.getFileType());
        assertEquals(multipartFile.getBytes().length, savedFile.getFileContent().length);
        assertThrowsWithMessage(
                () -> fileService.createFile(null),
                SystemException.class,
                "Input file cannot be null"
        );
    }

    @Order(4)
    @Test
    @DisplayName("Test create file with IO exception")
    public void testCreateFileWithIOException() throws IOException {
        final MultipartFile mockedFile = Mockito.mock(MultipartFile.class);

        when(mockedFile.getContentType()).thenReturn("text/plain");
        doThrow(new IOException("Simulated IOException")).when(mockedFile).getBytes();

        assertThrowsWithMessage(
                () -> fileService.createFile(mockedFile),
                SystemException.class,
                "Simulated IOException");
    }

}