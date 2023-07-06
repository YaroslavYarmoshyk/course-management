package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder(builderMethodName = "courseMark", setterPrefix = "with")
public class CourseMark {
    private Long courseCode;
    private Long studentId;
    private Map<Long, BigDecimal> lessonMarks;
    private BigDecimal markValue;
    private Mark mark;
}
