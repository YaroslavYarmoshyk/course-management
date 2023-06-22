package com.coursemanagement.service;

import com.coursemanagement.model.File;
import com.coursemanagement.model.HomeworkSubmission;
import org.springframework.web.multipart.MultipartFile;

public interface HomeworkService {

    HomeworkSubmission uploadHomework(final Long studentId, final Long lessonId, final MultipartFile multipartFile);

    File downloadHomework(final Long fileId);
}
