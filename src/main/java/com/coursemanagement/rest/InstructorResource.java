package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/instructor")
@InstructorAccessLevel
@RequiredArgsConstructor
public class InstructorResource {
    private final MarkService markService;

    private final CourseService courseService;
    private final FeedbackService feedbackService;

    @PostMapping(value = "/assign-mark")
    public MarkAssignmentResponseDto markLesson(@CurrentUser final User user,
                                                @RequestBody final MarkAssigmentRequestDto markAssigmentRequestDto) {
        return markService.assignMarkToUserLesson(user.getId(), markAssigmentRequestDto);
    }

    @PostMapping(value = "feedback")
    public FeedbackResponseDto provideFeedback(@CurrentUser final User user,
                                               @RequestBody final FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.provideFeedbackToUserCourse(user, feedbackRequestDto);
    }

    @GetMapping(value = "/courses")
    public Set<CourseDto> getInstructorCourses(@CurrentUser final User user) {
        return courseService.getCoursesByUserId(user.getId());
    }

    @GetMapping(value = "/courses/{course-code}/students")
    public Set<UserDto> getStudentsPerCourse(@PathVariable(value = "course-code") final Long courseCode) {
        return courseService.getStudentsByCourseCode(courseCode);
    }
}