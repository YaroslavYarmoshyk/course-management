package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import jakarta.validation.constraints.NotNull;

public record MarkAssignmentRequestDto(
        @NotNull Long instructorId,
        @NotNull Long studentId,
        @NotNull Long lessonId,
        @NotNull Mark mark
) {
}
