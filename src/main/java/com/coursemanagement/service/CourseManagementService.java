package com.coursemanagement.service;

import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;

public interface CourseManagementService {

    CourseAssignmentResponseDto assignInstructorToCourse(final Long instructorId, final Long courseCode);

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    CourseDto completeStudentCourse(final Long studentId, final Long courseCode);
}
