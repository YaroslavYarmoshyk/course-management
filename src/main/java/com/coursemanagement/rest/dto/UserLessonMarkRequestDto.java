package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;

public record UserLessonMarkRequestDto(Long studentId, Long lessonId, Mark mark) {
}
