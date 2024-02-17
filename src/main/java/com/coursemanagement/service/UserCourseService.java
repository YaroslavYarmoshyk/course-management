package com.coursemanagement.service;

import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserCourseService {

    UserCourse getUserCourse(final Long userId, final Long courseCode);

    Set<UserCourse> getUserCoursesByUserId(final Long userId);

    Set<UserCourseDto> getUserCourseSummariesByUserId(final Long userId);

    UserCourse saveUserCourse(final UserCourse userCourse);

    List<UserDto> getStudentsByCourseCode(final Long courseCode);

    UserCourseDetailsDto getUserCourseDetails(final Long userId, final Long courseCode);
}
