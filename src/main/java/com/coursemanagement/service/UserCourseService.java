package com.coursemanagement.service;

import com.coursemanagement.model.UserCourse;

import java.util.Set;

public interface UserCourseService {

    Set<UserCourse> getAllByUserId(final Long userId);
}
