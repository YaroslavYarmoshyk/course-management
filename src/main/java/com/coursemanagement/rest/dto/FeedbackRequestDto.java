package com.coursemanagement.rest.dto;

public record FeedbackRequestDto(Long studentId, Long courseCode, String feedback) {
}
