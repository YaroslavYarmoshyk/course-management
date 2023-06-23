package com.coursemanagement.service;

import com.coursemanagement.model.File;
import com.coursemanagement.model.Homework;
import org.springframework.web.multipart.MultipartFile;

public interface HomeworkService {

    Homework uploadHomework(final Long studentId, final Long lessonId, final MultipartFile multipartFile);

    File downloadHomework(final Long fileId);
}
