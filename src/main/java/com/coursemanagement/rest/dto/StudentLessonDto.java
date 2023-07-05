package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
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
        @JsonIgnoreProperties(value = {"lessonId", "studentId"})
        BigDecimal averageMark,
        Mark finalMark
) {
    public StudentLessonDto(final LessonDto lessonDto) {
        this(
                lessonDto.id(),
                lessonDto.title(),
                lessonDto.description(),
                lessonDto.lessonContent(),
                null,
                null
        );
    }

    public StudentLessonDto(final LessonDto lessonDto, final BigDecimal averageMark) {
        this(
                lessonDto.id(),
                lessonDto.title(),
                lessonDto.description(),
                lessonDto.lessonContent(),
                averageMark,
                Mark.of(averageMark)
        );
    }
}
