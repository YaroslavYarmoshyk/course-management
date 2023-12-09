package com.coursemanagement.rest.dto;

import com.coursemanagement.repository.entity.CourseFeedbackEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

public record CourseFeedbackDto(
        Long instructorId,
        String feedback,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
        LocalDateTime feedbackSubmissionDate
) {
    public CourseFeedbackDto(final CourseFeedbackEntity courseFeedbackEntity) {
        this(
                courseFeedbackEntity.getInstructorId(),
                courseFeedbackEntity.getFeedback(),
                courseFeedbackEntity.getFeedbackSubmissionDate()
        );
    }
}
