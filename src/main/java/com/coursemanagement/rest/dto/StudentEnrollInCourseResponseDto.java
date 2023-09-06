package com.coursemanagement.rest.dto;

import java.util.Set;

public record StudentEnrollInCourseResponseDto(
        Long studentId,
        Set<UserCourseDto> studentCourses
) {
}
