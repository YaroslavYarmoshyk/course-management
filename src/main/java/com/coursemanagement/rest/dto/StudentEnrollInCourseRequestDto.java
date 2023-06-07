package com.coursemanagement.rest.dto;

import java.util.Set;

public record StudentEnrollInCourseRequestDto(
        Long studentId,
        Set<Long> courseCodes
) {
}
