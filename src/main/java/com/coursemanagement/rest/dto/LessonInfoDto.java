package com.coursemanagement.rest.dto;

import com.coursemanagement.repository.entity.LessonEntity;

public record LessonInfoDto(
        Long id,
        String title,
        String description
) {
    public LessonInfoDto(final LessonEntity lessonEntity) {
        this(
                lessonEntity.getId(),
                lessonEntity.getTitle(),
                lessonEntity.getDescription()
        );
    }
}
