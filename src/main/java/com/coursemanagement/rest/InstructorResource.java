package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/instructor")
@InstructorAccessLevel
@RequiredArgsConstructor
public class InstructorResource {
    private final MarkService markService;
    private final FeedbackService feedbackService;

    @PostMapping(value = "/assign-mark")
    public MarkAssignmentResponseDto markLesson(@CurrentUser User instructor,
                                                @RequestBody MarkAssigmentRequestDto markAssigmentRequestDto) {
        return markService.assignMarkToUserLesson(instructor.getId(), markAssigmentRequestDto);
    }

    @PostMapping(value = "feedback")
    public FeedbackResponseDto provideFeedback(@CurrentUser User instructor,
                                               @RequestBody FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.provideFeedbackToUserCourse(instructor, feedbackRequestDto);
    }
}