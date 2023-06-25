package com.coursemanagement.service;

public interface LessonService {

    boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId);

    boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId);
}
