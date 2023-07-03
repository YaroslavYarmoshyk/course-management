package com.coursemanagement.service;

import com.coursemanagement.model.File;
import com.coursemanagement.model.Homework;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    Homework uploadHomework(final Long studentId, final Long lessonId, final MultipartFile multipartFile);

    File downloadHomework(final Long fileId);
}
