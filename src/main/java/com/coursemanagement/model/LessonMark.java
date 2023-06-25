package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LessonMark {
    private Long id;
    private Mark mark;
    private LocalDateTime markSubmissionDate;
    private Long lessonId;
    private Long studentId;
    private Long instructorId;
}
