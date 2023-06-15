package com.coursemanagement.service;

import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;

import java.util.Collection;
import java.util.Set;

public interface CourseService {

    Course getByCode(final Long code);

    Set<CourseDto> getAllByUserId(final Long userId);

    Set<UserCourse> getAllUserCoursesByUserId(final Long userId);

    CourseAssignmentResponseDto assignInstructor(CourseAssignmentRequestDto courseAssignmentRequestDto);

    void addUserToCourses(final User student, final Collection<Long> courses);
}
