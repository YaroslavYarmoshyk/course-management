package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

public record MarkAssignmentResponseDto(
        UserDto student,
        LessonInfoDto lesson,
        UserDto instructor,
        Mark mark,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN) LocalDateTime markSubmissionDate
) {
}
