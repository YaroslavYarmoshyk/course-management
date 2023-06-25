package com.coursemanagement.service;

import com.coursemanagement.model.CourseMark;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;

public interface MarkService {

    MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId,
                                                     final MarkAssigmentRequestDto markAssigmentRequestDto);

    CourseMark getStudentCourseMark(final Long studentId, final Long courseCode);
}
