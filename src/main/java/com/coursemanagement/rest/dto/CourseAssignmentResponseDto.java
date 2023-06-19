package com.coursemanagement.rest.dto;

import java.util.Set;

public record CourseAssignmentResponseDto(
        Long code,
        String title,
        Set<UserDto> instructors,
        Set<UserDto> students
) {
}
