package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.InstructorService;
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
    private final InstructorService instructorService;

    @PostMapping(value = "/assign-mark")
    public MarkAssignmentResponseDto markLesson(@CurrentUser final User user,
                                                @RequestBody final MarkAssigmentRequestDto markAssigmentRequestDto) {
        return instructorService.assignMarkToUserLesson(user.getId(), markAssigmentRequestDto);
    }

    @PostMapping(value = "/provide-feedback")
    public FeedbackResponseDto provideFeedback(@CurrentUser final User user,
                                               @RequestBody final FeedbackRequestDto feedbackRequestDto) {
        return instructorService.provideFeedbackToUserCourse(user, feedbackRequestDto);
    }
}