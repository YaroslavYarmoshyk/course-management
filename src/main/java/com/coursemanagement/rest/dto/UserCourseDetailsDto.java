package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.UserCourse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(setterPrefix = "with")
public record UserCourseDetailsDto(
        Long courseCode,
        String subject,
        String description,
        MarkInfoDto markInfo,
        Set<CourseFeedbackDto> courseFeedback,
        UserCourseStatus status,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
        LocalDateTime enrollmentDate,
        @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
        LocalDateTime accomplishmentDate
) {

    public static UserCourseDetailsDto of(final UserCourse userCourse,
                                          final CourseMark courseMark,
                                          final Set<CourseFeedbackDto> feedback) {
        final Course course = userCourse.getCourse();
        final MarkInfoDto markInfo = Optional.of(courseMark)
                .filter(markData -> !markData.getLessonMarks().isEmpty())
                .map(markData -> new MarkInfoDto(markData.getLessonMarks(), markData.getMarkValue(), markData.getMark()))
                .orElse(null);
        return UserCourseDetailsDto.builder()
                .withCourseCode(course.getCode())
                .withSubject(course.getSubject())
                .withDescription(course.getDescription())
                .withCourseFeedback(feedback)
                .withMarkInfo(markInfo)
                .withStatus(userCourse.getStatus())
                .withEnrollmentDate(userCourse.getEnrollmentDate())
                .withAccomplishmentDate(userCourse.getAccomplishmentDate())
                .build();
    }
}
