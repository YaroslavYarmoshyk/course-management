package com.coursemanagement.rest.dto;

import com.coursemanagement.model.Lesson;

public record LessonDto(Long id, String title, String description) {
    public LessonDto(final Lesson lesson) {
        this(lesson.getId(), lesson.getTitle(), lesson.getDescription());
    }
}
