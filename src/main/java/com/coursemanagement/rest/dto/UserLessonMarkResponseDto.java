package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.UserLesson;

public record UserLessonMarkResponseDto(UserDto student, LessonDto lesson, Mark mark) {
    public UserLessonMarkResponseDto(final UserLesson userLesson) {
        this(
                new UserDto(userLesson.getUser()),
                new LessonDto(userLesson.getLesson()),
                userLesson.getMark()
        );
    }
}
