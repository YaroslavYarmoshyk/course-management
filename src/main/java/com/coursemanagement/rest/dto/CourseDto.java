package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.entity.CourseEntity;

public record CourseDto(
        Long code,
        String title,
        String description,
        UserCourseStatus status
) {
    public CourseDto(final CourseEntity entity, final UserCourseStatus status) {
        this(
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                status
        );
    }

    public CourseDto(final UserCourse userCourse) {
        this(
                userCourse.getCourse().getCode(),
                userCourse.getCourse().getTitle(),
                userCourse.getCourse().getDescription(),
                userCourse.getStatus()
        );
    }
}
