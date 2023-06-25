package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StudentMark {
    private Long id;
    private Mark mark;
    private LocalDateTime markSubmissionDate;
    private Long lessonId;
    private Long studentId;
    private Long instructorId;

    public StudentMark(final Long studentId, final Long lessonId) {
        this.studentId = studentId;
        this.lessonId = lessonId;
    }
}
