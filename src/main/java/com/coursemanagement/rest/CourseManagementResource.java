package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.service.CourseManagementService;
import com.coursemanagement.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/course-management")
@RequiredArgsConstructor
public class CourseManagementResource {
    private final CourseManagementService courseManagementService;
    private final FeedbackService feedbackService;

    @AdminAccessLevel
    @PostMapping(value = "/assign-instructor")
    public CourseAssignmentResponseDto assignInstructor(@RequestBody final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        return courseManagementService.assignInstructorToCourse(courseAssignmentRequestDto);
    }

    @PostMapping(value = "/enrollments")
    public StudentEnrollInCourseResponseDto enrollInCourse(@RequestBody final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        return courseManagementService.enrollStudentInCourses(studentEnrollInCourseRequestDto);
    }

    @InstructorAccessLevel
    @PostMapping(value = "/provide-feedback")
    public FeedbackResponseDto provideFeedback(@CurrentUser final User instructor,
                                               @RequestBody final FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.provideFeedbackToUserCourse(instructor, feedbackRequestDto);
    }

    @PostMapping(value = "/complete")
    public UserCourseDetailsDto completeCourse(@RequestBody final CourseCompletionRequestDto courseCompletionRequestDto) {
        return courseManagementService.completeStudentCourse(courseCompletionRequestDto);
    }
}
