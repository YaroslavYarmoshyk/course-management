package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.UserCourse;

public record CourseDto(
        Long code,
        String title,
        String description,
        UserCourseStatus status
) {
    public CourseDto(final UserCourse userCourse) {
        this(
                userCourse.getCourse().getCode(),
                userCourse.getCourse().getTitle(),
                userCourse.getCourse().getDescription(),
                userCourse.getStatus()
        );
    }
}
