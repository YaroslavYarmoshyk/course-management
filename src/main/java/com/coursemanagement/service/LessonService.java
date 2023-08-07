package com.coursemanagement.service;

import com.coursemanagement.model.Lesson;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.StudentLessonDto;

import java.util.Set;

public interface LessonService {
    
    Set<Lesson> getLessonsPerCourse(final Long courseCode);

    Set<StudentLessonDto> getStudentLessonsWithContentPerCourse(final Long studentId, final Long courseCode);

    MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId, final MarkAssigmentRequestDto markAssigmentRequestDto);
}
