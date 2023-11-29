package com.coursemanagement.model;

import com.coursemanagement.enumeration.LessonPart;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LessonContent {
    private Long id;
    private LessonPart lessonPart;
    private Long fileId;
    private Long lessonId;

    @JsonCreator
    public LessonContent(@JsonProperty("id") final Long id,
                         @JsonProperty("lessonPart") final LessonPart lessonPart,
                         @JsonProperty("fileId") final Long fileId,
                         @JsonProperty("lessonId") final Long lessonId) {
        this.id = id;
        this.lessonPart = lessonPart;
        this.fileId = fileId;
        this.lessonId = lessonId;
    }
}
