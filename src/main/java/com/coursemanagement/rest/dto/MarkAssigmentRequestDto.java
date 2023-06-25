package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;

public record MarkAssigmentRequestDto(
        Long studentId,
        Long lessonId,
        Mark mark
) {
}
