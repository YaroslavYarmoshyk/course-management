package com.coursemanagement.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

@Builder(setterPrefix = "with")
public record FeedbackResponseDto(
        UserDto student,
        UserCourseDto userCourse,
        UserDto instructor,
        String feedback,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
        LocalDateTime feedbackSubmissionDate
) {
}
