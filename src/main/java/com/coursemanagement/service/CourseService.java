package com.coursemanagement.service;

import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;

import java.util.Collection;
import java.util.Set;

public interface CourseService {

    Course getByCode(final Long code);

    Set<Course> getAllByCodes(final Collection<Long> codes);

    Set<Course> getAllActiveByUserId(final Long userId);

    Set<CourseDto> getAllByUserId(final Long userId);

    Course save(final Course course);

    CourseAssignmentResponseDto assignInstructor(CourseAssignmentRequestDto courseAssignmentRequestDto);

    Set<Course> addStudentToCourses(final User student, final Collection<Course> courses);
}
