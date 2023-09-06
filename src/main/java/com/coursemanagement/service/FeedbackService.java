package com.coursemanagement.service;

import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;

public interface FeedbackService {

    FeedbackResponseDto provideFeedbackToUserCourse(final FeedbackRequestDto feedbackRequestDto);
}
