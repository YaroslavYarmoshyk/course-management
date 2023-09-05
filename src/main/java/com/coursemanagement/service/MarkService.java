package com.coursemanagement.service;

import com.coursemanagement.model.CourseMark;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;

import java.math.BigDecimal;
import java.util.Map;

public interface MarkService {

    MarkAssignmentResponseDto assignMarkToStudentLesson(final Long instructorId,
                                                        final MarkAssigmentRequestDto markAssigmentRequestDto);

    Map<Long, BigDecimal> getAverageLessonMarksForStudentPerCourse(final Long studentId, final Long courseCode);

    CourseMark getStudentCourseMark(final Long studentId, final Long courseCode);
}
