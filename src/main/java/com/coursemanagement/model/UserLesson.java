package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLesson {
    private Long id;
    private User user;
    private Lesson lesson;
    private Mark mark;
}
