package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.UserLesson;

public record UserLessonMarkResponseDto(StudentDto student, LessonDto lesson, Mark mark) {
    public UserLessonMarkResponseDto(final UserLesson userLesson) {
        this(
                new StudentDto(userLesson.getUser()),
                new LessonDto(userLesson.getLesson()),
                userLesson.getMark()
        );
    }
}
