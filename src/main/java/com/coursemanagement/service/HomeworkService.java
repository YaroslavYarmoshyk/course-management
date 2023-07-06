package com.coursemanagement.service;

import com.coursemanagement.model.File;
import org.springframework.web.multipart.MultipartFile;

public interface HomeworkService {

    void uploadHomework(final Long studentId, final Long lessonId, final MultipartFile multipartFile);

    File downloadHomework(final Long studentId, final Long fileId);
}
