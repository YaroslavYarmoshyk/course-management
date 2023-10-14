package com.coursemanagement.rest.dto;

import jakarta.validation.constraints.NotNull;

public record InstructorAssignmentRequestDto(
        @NotNull Long instructorId,
        @NotNull Long courseCode
) {
}
