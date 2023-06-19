package com.coursemanagement.rest.dto;

public record CourseAssignmentRequestDto(
        Long userId,
        Long courseCode
) {
}
