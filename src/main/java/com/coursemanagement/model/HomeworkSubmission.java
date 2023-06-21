package com.coursemanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class HomeworkSubmission {
    private Long id;
    private String fileName;
    private LocalDateTime uploadedDate;
    private byte[] homework;
    private Long lessonId;
    private Long studentId;
}
