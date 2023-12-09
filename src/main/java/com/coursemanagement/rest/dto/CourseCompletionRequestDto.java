package com.coursemanagement.rest.dto;

import jakarta.validation.constraints.NotNull;

public record CourseCompletionRequestDto(
        @NotNull Long studentId,
        @NotNull Long courseCode
) {
}
