package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.InstructorService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final CourseService courseService;
    private final LessonService lessonService;
    private final MarkService markService;
    private final FeedbackService feedbackService;

    @Override
    public MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId, final MarkAssigmentRequestDto markAssigmentRequestDto) {
        validateMarkAssigment(markAssigmentRequestDto.studentId(), markAssigmentRequestDto.lessonId());
        return markService.assignMarkToUserLesson(instructorId, markAssigmentRequestDto);
    }

    private void validateMarkAssigment(final Long studentId, final Long lessonId) {
        if (!lessonService.isUserAssociatedWithLesson(studentId, lessonId)) {
            throw new SystemException("Student is not associated with lesson", SystemErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public FeedbackResponseDto provideFeedbackToUserCourse(final User instructor, final FeedbackRequestDto feedbackRequestDto) {
        validateUserCourseAssociation(feedbackRequestDto.studentId(), feedbackRequestDto.courseCode());
        return feedbackService.provideFeedbackToUserCourse(instructor, feedbackRequestDto);
    }

    private void validateUserCourseAssociation(final Long studentId, final Long courseCode) {
        if (!courseService.isUserAssociatedWithCourse(studentId, courseCode)) {
            throw new SystemException("Student is not associated with course", SystemErrorCode.BAD_REQUEST);
        }
    }
}
