package com.coursemanagement.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class CourseFeedback {
    private Long id;
    private Long courseCode;
    private Long studentId;
    private Long instructorId;
    private String feedback;
    private LocalDateTime feedbackDate;
}
