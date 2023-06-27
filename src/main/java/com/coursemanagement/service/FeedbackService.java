package com.coursemanagement.service;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;

public interface FeedbackService {

    FeedbackResponseDto provideFeedbackToUserCourse(final User instructor, final FeedbackRequestDto feedbackRequestDto);
}
