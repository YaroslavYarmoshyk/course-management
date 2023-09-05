package com.coursemanagement.service;

import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;

public interface CourseManagementService {

    CourseAssignmentResponseDto assignInstructorToCourse(final Long instructorId, final Long courseCode);

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    UserCourseDetailsDto completeStudentCourse(final CourseCompletionRequestDto courseCompletionRequestDto);
}
