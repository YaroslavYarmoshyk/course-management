package com.coursemanagement.service;

import com.coursemanagement.model.Lesson;
import com.coursemanagement.rest.dto.GradeAssigmentRequestDto;
import com.coursemanagement.rest.dto.GradeAssignmentResponseDto;

public interface LessonService {

    Lesson getLessonById(final Long lessonId);

    GradeAssignmentResponseDto assignGradeToUserLesson(final GradeAssigmentRequestDto gradeAssigmentRequestDto);
}
