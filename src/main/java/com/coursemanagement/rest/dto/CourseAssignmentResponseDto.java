package com.coursemanagement.rest.dto;

import java.util.Set;

public record CourseAssignmentResponseDto(
        Long code,
        String subject,
        Set<UserInfoDto> instructors,
        Set<UserInfoDto> students
) {
}
