package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.StudentCourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.StudentLessonDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseManagementService;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/courses")
@RequiredArgsConstructor
public class CourseResource {
    private final CourseService courseService;
    private final LessonService lessonService;
    private final FeedbackService feedbackService;
    private final CourseManagementService courseManagementService;

    @GetMapping
    public Set<CourseDto> getUserCourses(@CurrentUserId final Long userId) {
        return courseService.getCoursesByUserId(userId);
    }

    @PostMapping(value = "/enrollments")
    public StudentEnrollInCourseResponseDto enrollInCourse(@RequestBody final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        return courseManagementService.enrollStudentInCourses(studentEnrollInCourseRequestDto);
    }

    @AdminAccessLevel
    @PostMapping(value = "/assign-instructor")
    public CourseAssignmentResponseDto assignInstructor(@RequestBody final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        return courseManagementService.assignInstructorToCourse(
                courseAssignmentRequestDto.instructorId(),
                courseAssignmentRequestDto.courseCode()
        );
    }

    @InstructorAccessLevel
    @PostMapping(value = "/provide-feedback")
    public FeedbackResponseDto provideFeedback(@CurrentUser final User instructor,
                                               @RequestBody final FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.provideFeedbackToUserCourse(instructor, feedbackRequestDto);
    }

    @InstructorAccessLevel
    @GetMapping(value = "/{course-code}/students")
    public Set<UserDto> getStudentsPerCourse(@PathVariable(value = "course-code") final Long courseCode) {
        return courseService.getStudentsByCourseCode(courseCode);
    }

    @GetMapping(value = "/{course-code}/lessons")
    public Set<StudentLessonDto> getLessonsPerCourse(@CurrentUserId final Long userId,
                                                     @PathVariable(value = "course-code") final Long courseCode) {
        return lessonService.getStudentLessonsWithContentPerCourse(userId, courseCode);
    }

    //    TODO: add /{course-code}/student-information endpoint which will return StudentCourseDto

    @GetMapping(value = "/{course-code}/complete")
    public StudentCourseDto completeCourse(@CurrentUserId final Long studentId,
                                           @PathVariable(value = "course-code") final Long courseCode) {
        return courseManagementService.completeStudentCourse(studentId, courseCode);
    }

    @GetMapping(value = "/{course-code}/final-mark")
    public CourseMark getStudentCourseMark(@CurrentUserId final Long studentId,
                                           @PathVariable(value = "course-code") final Long courseCode) {
        return courseService.getStudentCourseFinalMark(studentId, courseCode);
    }
}
