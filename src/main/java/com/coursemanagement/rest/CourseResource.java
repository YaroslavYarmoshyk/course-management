package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/courses")
@RequiredArgsConstructor
public class CourseResource {
    private final CourseService courseService;
    private final LessonService lessonService;

    @GetMapping
    public Set<CourseDto> getUserCourses(@CurrentUser final User user) {
        return courseService.getCoursesByUserId(user.getId());
    }

    @GetMapping(value = "/{course-code}/lessons")
    public Set<LessonDto> getLessonsPerCourse(@PathVariable(value = "course-code") final Long courseCode,
                                              @CurrentUser final User user) {
        return lessonService.getLessonsPerCourse(courseCode, user.getId());
    }

    @InstructorAccessLevel
    @GetMapping(value = "/{course-code}/students")
    public Set<UserDto> getStudentsPerCourse(@PathVariable(value = "course-code") final Long courseCode) {
        return courseService.getStudentsByCourseCode(courseCode);
    }

//    TODO: lbtm
    @InstructorAccessLevel
    @GetMapping(value = "/{course-code}/students/{student-id}/final-mark")
    public Set<UserDto> getStudentCourseMark(@PathVariable(value = "course-code") final Long courseCode,
                                             @PathVariable(value = "student-id") final Long studentId) {
        return courseService.getStudentsByCourseCode(courseCode);
    }
}
