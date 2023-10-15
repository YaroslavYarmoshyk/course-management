package com.coursemanagement.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record StudentEnrollInCourseRequestDto(
        @NotNull Long studentId,
        @NotNull Set<Long> courseCodes
) {
}
