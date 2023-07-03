package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;

public interface InstructorService {

    MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId,
                                                     final MarkAssigmentRequestDto markAssigmentRequestDto);

    FeedbackResponseDto provideFeedbackToUserCourse(final User instructor, final FeedbackRequestDto feedbackRequestDto);
}
