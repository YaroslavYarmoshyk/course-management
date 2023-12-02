package com.coursemanagement.rest.dto;

import jakarta.validation.constraints.NotNull;

public record FeedbackRequestDto(@NotNull Long instructorId,
                                 @NotNull Long studentId,
                                 @NotNull Long courseCode,
                                 @NotNull String feedback) {
}
