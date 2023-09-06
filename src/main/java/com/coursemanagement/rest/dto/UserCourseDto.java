package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.UserCourse;

public record UserCourseDto(
        Long code,
        String subject,
        String description,
        UserCourseStatus status
) {
    public UserCourseDto(final UserCourse userCourse) {
        this(
                userCourse.getCourse().getCode(),
                userCourse.getCourse().getSubject(),
                userCourse.getCourse().getDescription(),
                userCourse.getStatus()
        );
    }
}
