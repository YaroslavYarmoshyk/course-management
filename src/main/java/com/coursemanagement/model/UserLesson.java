package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserLesson {
    private Long id;
    private User student;
    private Lesson lesson;
    private User instructor;
    private Mark mark;
    private LocalDateTime markAppliedAt;
}
