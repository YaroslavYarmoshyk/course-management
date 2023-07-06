package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Set;

public record StudentLessonDto(
        Long id,
        String title,
        String description,
        @JsonIgnoreProperties(value = "lessonId")
        Set<LessonContent> lessonContent,
        BigDecimal averageMark,
        Mark finalMark
) {

    public StudentLessonDto(
            final Lesson lesson,
            final Set<LessonContent> lessonContent,
            final BigDecimal averageMark
    ) {
        this(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lessonContent,
                averageMark,
                Mark.of(averageMark)
        );
    }
}
