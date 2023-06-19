package com.coursemanagement.service;

import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface StudentService {

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    Set<CourseDto> getAllCoursesByStudentId(final Long userId);

    void uploadHomework(final Long studentId, final Long lessonId, final MultipartFile homework);
}
