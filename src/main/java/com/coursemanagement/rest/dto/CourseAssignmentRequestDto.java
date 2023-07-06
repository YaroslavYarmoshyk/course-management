package com.coursemanagement.rest.dto;

public record CourseAssignmentRequestDto(
        Long instructorId,
        Long courseCode
) {
}
