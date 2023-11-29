package com.coursemanagement.service;

import com.coursemanagement.model.Lesson;
import com.coursemanagement.rest.dto.MarkAssignmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserLessonDto;

import java.util.Set;

public interface LessonService {
    
    Set<Lesson> getLessonsPerCourse(final Long courseCode);

    Set<UserLessonDto> getUserLessonsWithContentPerCourse(final Long userId, final Long courseCode);

    MarkAssignmentResponseDto assignMarkToUserLesson(final MarkAssignmentRequestDto markAssignmentRequestDto);
}
