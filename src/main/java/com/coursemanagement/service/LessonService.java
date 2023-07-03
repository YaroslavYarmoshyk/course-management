package com.coursemanagement.service;

import com.coursemanagement.rest.dto.LessonDto;

import java.util.Set;

public interface LessonService {

    boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId);

    boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId);

    Set<LessonDto> getLessonsPerCourse(final Long courseCode, final Long userId);
}
