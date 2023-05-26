package com.coursemanagement.service;

import com.coursemanagement.model.Course;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;

public interface CourseService {

    Course findByCode(final Long code);

    Course save(final Course course);

    CourseAssignmentResponseDto assignInstructor(CourseAssignmentRequestDto courseAssignmentRequestDto);
}
