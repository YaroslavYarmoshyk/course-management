package com.coursemanagement.rest;

import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/student")
@RequiredArgsConstructor
public class StudentResource {
    private final StudentService studentService;

    @PostMapping(value = "/course/enrollments")
    public StudentEnrollInCourseResponseDto enrollInCourse(@RequestBody StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        return studentService.enrollStudentInCourses(studentEnrollInCourseRequestDto);
    }

    @GetMapping(value = "{studentId}/courses")
    public Set<CourseDto> getAllCoursesForCurrentUser(@PathVariable(value = "studentId") final Long studentId) {
        return studentService.getAllCoursesByStudentId(studentId);
    }
}
