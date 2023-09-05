package com.coursemanagement.rest.dto;

public record CourseCompletionRequestDto(
        Long studentId,
        Long courseCode
) {
}
