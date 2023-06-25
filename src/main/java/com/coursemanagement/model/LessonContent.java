package com.coursemanagement.model;

import com.coursemanagement.enumeration.LessonPart;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonContent {
    private Long id;
    private LessonPart lessonPart;
    private Long fileId;
    private Long lessonId;
}
