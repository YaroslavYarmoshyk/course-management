package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

public record MarkAssignmentResponseDto(
        UserInfoDto student,
        LessonDto lesson,
        UserInfoDto instructor,
        Mark mark,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN) LocalDateTime mark_submission_date
) {
}
