package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;

public record GradeAssigmentRequestDto(
        Long studentId,
        Long lessonId,
        Mark mark
) {
}
