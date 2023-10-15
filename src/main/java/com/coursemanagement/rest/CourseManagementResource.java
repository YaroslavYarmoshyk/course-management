package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.rest.dto.InstructorAssignmentRequestDto;
import com.coursemanagement.rest.dto.InstructorAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.service.CourseManagementService;
import com.coursemanagement.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.coursemanagement.util.Constants.COURSE_MANAGEMENT_ENDPOINT;

@RestController
@RequestMapping(value = COURSE_MANAGEMENT_ENDPOINT)
@RequiredArgsConstructor
public class CourseManagementResource {
    private final CourseManagementService courseManagementService;
    private final FeedbackService feedbackService;

    @AdminAccessLevel
    @PostMapping(value = "/assign-instructor")
    public InstructorAssignmentResponseDto assignInstructor(@Valid @RequestBody final InstructorAssignmentRequestDto instructorAssignmentRequestDto) {
        return courseManagementService.assignInstructorToCourse(instructorAssignmentRequestDto);
    }

    @PostMapping(value = "/enrollments")
    public StudentEnrollInCourseResponseDto enrollInCourse(@Valid @RequestBody final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        return courseManagementService.enrollStudentInCourses(studentEnrollInCourseRequestDto);
    }

    @InstructorAccessLevel
    @PostMapping(value = "/provide-feedback")
    public FeedbackResponseDto provideFeedback(@RequestBody final FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.provideFeedbackToUserCourse(feedbackRequestDto);
    }

    @PostMapping(value = "/complete")
    public UserCourseDetailsDto completeCourse(@RequestBody final CourseCompletionRequestDto courseCompletionRequestDto) {
        return courseManagementService.completeStudentCourse(courseCompletionRequestDto);
    }
}
