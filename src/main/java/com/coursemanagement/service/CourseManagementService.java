package com.coursemanagement.service;

import com.coursemanagement.rest.dto.InstructorAssignmentRequestDto;
import com.coursemanagement.rest.dto.InstructorAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;

public interface CourseManagementService {

    InstructorAssignmentResponseDto assignInstructorToCourse(final InstructorAssignmentRequestDto instructorAssignmentRequestDto);

    StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto);

    UserCourseDetailsDto completeStudentCourse(final CourseCompletionRequestDto courseCompletionRequestDto);
}
