package com.coursemanagement.service;

import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;

public interface CourseManagementService {

    CourseAssignmentResponseDto assignInstructorToCourse(final Long instructorId, final Long courseCode);

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    UserCourseDetailsDto completeStudentCourse(final Long studentId, final Long courseCode);
}
