package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Mark;

import java.math.BigDecimal;
import java.util.Map;

public record MarkInfoDto(
        Map<Long, BigDecimal>lessonMarks,
        BigDecimal markValue,
        Mark mark
) {
}
