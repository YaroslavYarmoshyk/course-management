package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/courses")
@RequiredArgsConstructor
public class CourseResource {
    private final CourseService courseService;
    private final UserCourseService userCourseService;
    private final LessonService lessonService;

    @GetMapping
    public Set<UserCourseDto> getUserCourses(@CurrentUserId final Long userId) {
        return userCourseService.getUserCourseSummariesByUserId(userId);
    }

    @GetMapping(value = "/{course-code}")
    public UserCourseDetailsDto getUserCourseDetails(@CurrentUserId final Long userId,
                                                     @PathVariable(value = "course-code") final Long courseCode) {
        return userCourseService.getUserCourseDetails(userId, courseCode);
    }

    @InstructorAccessLevel
    @GetMapping(value = "/{course-code}/students")
    public Set<UserDto> getStudentsPerCourse(@PathVariable(value = "course-code") final Long courseCode) {
        return userCourseService.getStudentsByCourseCode(courseCode);
    }

    @GetMapping(value = "/{course-code}/lessons")
    public Set<UserLessonDto> getLessonsPerCourse(@CurrentUserId final Long userId,
                                                  @PathVariable(value = "course-code") final Long courseCode) {
        return lessonService.getUserLessonsWithContentPerCourse(userId, courseCode);
    }

    @GetMapping(value = "/{course-code}/final-mark")
    public CourseMark getStudentCourseMark(@CurrentUserId final Long studentId,
                                           @PathVariable(value = "course-code") final Long courseCode) {
        return courseService.getStudentCourseFinalMark(studentId, courseCode);
    }
}
