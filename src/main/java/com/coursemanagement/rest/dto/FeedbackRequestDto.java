package com.coursemanagement.rest.dto;

public record FeedbackRequestDto(Long instructorId,
                                 Long studentId,
                                 Long courseCode,
                                 String feedback) {
}
