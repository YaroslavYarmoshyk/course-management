package com.coursemanagement.model;

import com.coursemanagement.enumeration.Mark;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CourseMark {
    private Long courseCode;
    private Long studentId;
    private BigDecimal markValue;
    private Mark mark;
}
