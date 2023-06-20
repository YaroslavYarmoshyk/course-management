package com.coursemanagement.service;

import com.coursemanagement.model.UserLesson;
import com.coursemanagement.rest.dto.UserLessonMarkRequestDto;

public interface LessonService {

    UserLesson createUserLesson(Long userId, Long lessonId);

    UserLesson getUserLesson(final Long userId, final Long lessonId);

    UserLesson markLesson(UserLessonMarkRequestDto userLessonMarkRequestDto);
}
