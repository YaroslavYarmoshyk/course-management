package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.File;
import com.coursemanagement.repository.HomeworkRepository;
import com.coursemanagement.repository.entity.HomeworkEntity;
import com.coursemanagement.service.FileService;
import com.coursemanagement.service.HomeworkService;
import com.coursemanagement.service.UserAssociationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final UserAssociationService userAssociationService;
    private final FileService fileService;

    @Override
    @Transactional
    public void uploadHomework(final Long studentId,
                               final Long lessonId,
                               final MultipartFile multipartFile) {
        validateHomeworkUploading(studentId, lessonId);
        final File file = fileService.createFile(multipartFile);
        final HomeworkEntity homeworkEntity = new HomeworkEntity()
                .setFileId(file.getId())
                .setStudentId(studentId)
                .setLessonId(lessonId)
                .setUploadedDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        homeworkRepository.save(homeworkEntity);
    }

    private void validateHomeworkUploading(final Long studentId, final Long lessonId) {
        if (!userAssociationService.isUserAssociatedWithLesson(studentId, lessonId)) {
            throw new SystemException("Access to the homework uploading is limited to associated lesson only", SystemErrorCode.FORBIDDEN);
        }
    }

    @Override
    public File downloadHomework(final Long studentId, final Long fileId) {
        validateHomeworkDownloading(studentId, fileId);
        return fileService.getFileById(fileId);
    }

    private void validateHomeworkDownloading(final Long studentId, final Long fileId) {
        if (!userAssociationService.isUserAssociatedWithLessonFile(studentId, fileId)) {
            throw new SystemException("Access to the homework downloading is limited to associated lesson only", SystemErrorCode.FORBIDDEN);
        }
    }
}
