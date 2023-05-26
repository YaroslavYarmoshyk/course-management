package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/course")
@RequiredArgsConstructor
public class CourseResource {
    private final CourseService courseService;

    @AdminAccessLevel
    @PostMapping(value = "/assign-instructor")
    public CourseAssignmentResponseDto assignInstructor(@RequestBody CourseAssignmentRequestDto courseAssignmentRequestDto) {
        return courseService.assignInstructor(courseAssignmentRequestDto);
    }
}
