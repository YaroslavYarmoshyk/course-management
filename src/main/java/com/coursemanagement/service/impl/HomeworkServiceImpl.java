package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.File;
import com.coursemanagement.model.Homework;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.HomeworkRepository;
import com.coursemanagement.repository.entity.HomeworkEntity;
import com.coursemanagement.service.FileService;
import com.coursemanagement.service.HomeworkService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final FileService fileService;
    private final UserService userService;
    private final LessonService lessonService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public Homework uploadHomework(final Long studentId,
                                   final Long lessonId,
                                   final MultipartFile multipartFile) {
        validateHomeworkUploading(studentId, lessonId);
        final File file = createFile(multipartFile);
        final HomeworkEntity homeworkEntity = new HomeworkEntity()
                .setFileId(file.getId())
                .setStudentId(studentId)
                .setLessonId(lessonId)
                .setUploadedDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        final HomeworkEntity savedHomework = homeworkRepository.save(homeworkEntity);
        return mapper.map(savedHomework, Homework.class);
    }

    private void validateHomeworkUploading(final Long studentId, final Long lessonId) {
        if (!lessonService.isUserAssociatedWithLesson(studentId, lessonId)) {
            throw new SystemException("Access to the homework uploading is limited to associated lesson only", SystemErrorCode.FORBIDDEN);
        }
    }

    private File createFile(final MultipartFile multipartFile) {
        try {
            final FileType fileType = Optional.ofNullable(multipartFile)
                    .map(MultipartFile::getContentType)
                    .map(FileType::of)
                    .orElseThrow(() -> new SystemException("Invalid input file: " + multipartFile, SystemErrorCode.BAD_REQUEST));
            final File file = new File()
                    .setFileName(FilenameUtils.removeExtension(multipartFile.getOriginalFilename()))
                    .setFileType(fileType)
                    .setFileContent(multipartFile.getBytes());
            return fileService.save(file);
        } catch (final IOException e) {
            throw new SystemException(e.getMessage(), SystemErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public File downloadHomework(final Long fileId) {
        final User user = userService.resolveCurrentUser();
        final boolean userAssociatedWithFile = lessonService.isUserAssociatedWithLessonFile(user.getId(), fileId);
        if (userAssociatedWithFile) {
            return fileService.getFileById(fileId);
        }
        throw new SystemException("User is not allowed to download this file", SystemErrorCode.FORBIDDEN);
    }
}
