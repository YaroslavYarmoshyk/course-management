package com.coursemanagement.rest.dto;

import com.coursemanagement.repository.entity.LessonEntity;

public record LessonDto(
        Long id,
        String title,
        String description
) {
    public LessonDto(final LessonEntity lessonEntity) {
        this(
                lessonEntity.getId(),
                lessonEntity.getTitle(),
                lessonEntity.getDescription()
        );
    }
}
