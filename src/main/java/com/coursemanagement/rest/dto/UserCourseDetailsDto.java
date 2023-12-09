package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.UserCourse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Set;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

public record UserCourseDetailsDto(
        Long courseCode,
        String subject,
        String description,
        @JsonIgnoreProperties(value = {"courseCode", "studentId"})
        CourseMark courseMark,
        Set<CourseFeedbackDto> courseFeedback,
        UserCourseStatus status,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
        LocalDateTime enrollmentDate,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
        LocalDateTime accomplishmentDate
) {
    public UserCourseDetailsDto(final UserCourse userCourse, final CourseMark courseMark, final Set<CourseFeedbackDto> feedback) {
        this(
                userCourse.getCourse().getCode(),
                userCourse.getCourse().getSubject(),
                userCourse.getCourse().getDescription(),
                courseMark,
                feedback,
                userCourse.getStatus(),
                userCourse.getEnrollmentDate(),
                userCourse.getAccomplishmentDate()
        );
    }
}
