package com.coursemanagement.service;

public interface UserAssociationService {

    boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode);

    boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId);

    boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId);
}
