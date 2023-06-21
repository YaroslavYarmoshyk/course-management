package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Grade {
    private Long id;
    private Mark mark;
    private LocalDateTime markSubmissionDate;
    private Long lessonId;
    private Long studentId;
    private Long instructorId;

    public Grade(final Long studentId, final Long lessonId) {
        this.studentId = studentId;
        this.lessonId = lessonId;
    }
}
