package com.coursemanagement.service;

import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;

public interface CourseManagementService {

    CourseAssignmentResponseDto assignInstructorToCourse(final CourseAssignmentRequestDto courseAssignmentRequestDto);

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    UserCourseDetailsDto completeStudentCourse(final CourseCompletionRequestDto courseCompletionRequestDto);
}
