package com.coursemanagement.service;

import com.coursemanagement.rest.dto.GradeAssigmentRequestDto;
import com.coursemanagement.rest.dto.GradeAssignmentResponseDto;
import com.coursemanagement.rest.dto.LessonDto;

public interface LessonService {

    LessonDto getById(final Long lessonId);

    GradeAssignmentResponseDto assignGradeToUserLesson(final GradeAssigmentRequestDto gradeAssigmentRequestDto);
}
