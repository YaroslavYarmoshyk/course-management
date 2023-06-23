package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.File;
import com.coursemanagement.model.HomeworkSubmission;
import com.coursemanagement.repository.HomeworkRepository;
import com.coursemanagement.repository.entity.HomeworkSubmissionEntity;
import com.coursemanagement.service.FileService;
import com.coursemanagement.service.HomeworkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final FileService fileService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public HomeworkSubmission uploadHomework(final Long studentId,
                                             final Long lessonId,
                                             final MultipartFile multipartFile) {
        final File file = createFile(multipartFile);
        final HomeworkSubmissionEntity homeworkSubmissionEntity = new HomeworkSubmissionEntity()
                .setFileId(file.getId())
                .setStudentId(studentId)
                .setLessonId(lessonId)
                .setUploadedDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        final HomeworkSubmissionEntity savedHomework = homeworkRepository.save(homeworkSubmissionEntity);
        return mapper.map(savedHomework, HomeworkSubmission.class);
    }

    private File createFile(final MultipartFile multipartFile) {
        try {
            final FileType fileType = Optional.ofNullable(multipartFile)
                    .map(MultipartFile::getContentType)
                    .map(FileType::of)
                    .orElseThrow(() -> new SystemException("Invalid input file: " + multipartFile, SystemErrorCode.BAD_REQUEST));
            final File file = new File()
                    .setFileName(multipartFile.getOriginalFilename())
                    .setFileType(fileType)
                    .setFileContent(multipartFile.getBytes());
            return fileService.save(file);
        } catch (final IOException e) {
            throw new SystemException(e.getMessage(), SystemErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public File downloadHomework(final Long fileId) {
        return fileService.getFileById(fileId);
    }
}
