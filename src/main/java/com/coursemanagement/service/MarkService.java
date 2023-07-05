package com.coursemanagement.service;

import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.LessonMark;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;

import java.util.Set;

public interface MarkService {

    MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId,
                                                     final MarkAssigmentRequestDto markAssigmentRequestDto);

    CourseMark getStudentCourseMark(final Long studentId, final Long courseCode);

    Set<LessonMark> getStudentLessonMarksByCourseCode(final Long studentId, final Long courseCode);
}
