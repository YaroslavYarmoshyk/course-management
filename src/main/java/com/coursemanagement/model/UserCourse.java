package com.coursemanagement.model;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCourse {
    private User user;
    private Course course;
    private UserCourseStatus status;

    @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
    private LocalDateTime enrollmentDate;

    @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
    private LocalDateTime accomplishmentDate;
}
