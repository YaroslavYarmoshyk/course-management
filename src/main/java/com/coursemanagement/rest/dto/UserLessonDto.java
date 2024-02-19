package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserLessonDto(
        Long lessonId,
        String title,
        String description,
        @JsonIgnoreProperties(value = "lessonId")
        Set<LessonContent> lessonContent,
        BigDecimal markValue,
        Mark mark
) {

    public UserLessonDto(
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
