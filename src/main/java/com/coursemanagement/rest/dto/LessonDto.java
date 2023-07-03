package com.coursemanagement.rest.dto;

import com.coursemanagement.model.LessonContent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

public record LessonDto(
        Long id,
        String title,
        String description,
        @JsonIgnoreProperties(value = "lessonId")
        Set<LessonContent> lessonContent
) {
}
