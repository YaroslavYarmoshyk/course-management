package com.coursemanagement.service;

import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.UserDto;

import java.util.Collection;
import java.util.Set;

public interface CourseService {

    Course getCourseByCode(final Long code);

    Set<UserCourse> getUserCoursesByUserId(final Long userId);

    Set<CourseDto> getCoursesByUserId(final Long userId);

    Set<UserDto> getStudentsByCourseCode(final Long courseCode);

    void addUserToCourses(final User student, final Collection<Long> courses);

    boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode);
}
