package com.coursemanagement.service;

import com.coursemanagement.model.Lesson;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;

public interface LessonService {

    Lesson getLessonById(final Long lessonId);

    MarkAssignmentResponseDto assignMarkToUserLesson(final MarkAssigmentRequestDto markAssigmentRequestDto);

    boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId);

    boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId);
}
