package com.coursemanagement.service;

import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserLesson;

import java.util.Collection;
import java.util.Set;

public interface LessonService {

    Set<Lesson> getAllByCourseCodes(final Collection<Long> courseCodes);

    void addUserToLessons(final User user, final Collection<Lesson> lessons);

    UserLesson getUserLesson(final Long userId, final Long lessonId);
}
