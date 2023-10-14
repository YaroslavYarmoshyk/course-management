package com.coursemanagement.rest.dto;

import java.util.Set;

public record InstructorAssignmentResponseDto(
        Long code,
        String subject,
        Set<UserDto> instructors,
        Set<UserDto> students
) {
}
