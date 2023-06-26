package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(builderMethodName = "courseMark", setterPrefix = "with")
public class CourseMark {
    private Long courseCode;
    private Long studentId;
    private BigDecimal markValue;
    private Mark mark;
}
