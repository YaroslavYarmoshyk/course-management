package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder(builderMethodName = "courseMark", setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class CourseMark {
    private Long courseCode;
    private Long studentId;
    private Map<Long, BigDecimal> lessonMarks;
    private BigDecimal markValue;
    private Mark mark;
}
