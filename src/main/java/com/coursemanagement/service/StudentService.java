package com.coursemanagement.service;

import com.coursemanagement.model.File;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.StudentLessonDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface StudentService {

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    void uploadHomework(final Long studentId, final Long lessonId, final MultipartFile multipartFile);

    File downloadHomework(final Long studentId, final Long fileId);

    Set<StudentLessonDto> getStudentLessonsPerCourse(final Long studentId, final Long courseCode);
}
