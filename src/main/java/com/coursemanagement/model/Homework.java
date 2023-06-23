package com.coursemanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN;

@Data
@NoArgsConstructor
public class Homework {
    private Long id;
    @JsonFormat(pattern = DEFAULT_DATE_FORMAT_PATTERN)
    private LocalDateTime uploadedDate;
    private Long fileId;
    private Long lessonId;
    private Long studentId;
}
