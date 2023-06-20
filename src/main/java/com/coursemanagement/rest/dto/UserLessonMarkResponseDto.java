package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.UserLesson;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

public record UserLessonMarkResponseDto(
        UserDto student,
        LessonDto lesson,
        UserDto instructor,
        Mark mark,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN) LocalDateTime markAppliedAt
) {
    public UserLessonMarkResponseDto(final UserLesson userLesson) {
        this(
                new UserDto(userLesson.getStudent()),
                new LessonDto(userLesson.getLesson()),
                new UserDto(userLesson.getInstructor()),
                userLesson.getMark(),
                userLesson.getMarkAppliedAt()
        );
    }
}
