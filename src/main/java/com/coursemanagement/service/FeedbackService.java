package com.coursemanagement.service;

import com.coursemanagement.rest.dto.CourseFeedbackDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;

import java.util.Set;

public interface FeedbackService {

    FeedbackResponseDto provideFeedbackToUserCourse(final FeedbackRequestDto feedbackRequestDto);

    Set<CourseFeedbackDto> getTotalCourseFeedback(final Long studentId, final Long courseCode);
}
