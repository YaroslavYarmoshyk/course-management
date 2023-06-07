package com.coursemanagement.rest.dto;

import com.coursemanagement.repository.entity.CourseEntity;

public record CourseDto(
        Long code,
        String title,
        String description,
        boolean active
) {
    public CourseDto(final CourseEntity entity, final boolean active) {
        this(entity.getCode(), entity.getTitle(), entity.getDescription(), active);
    }
}
