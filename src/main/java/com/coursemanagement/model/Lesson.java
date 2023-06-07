package com.coursemanagement.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Lesson {
    private Long id;
    private Course course;
    private String title;
    private String description;
    private BigDecimal credits;
    private byte[] homework;
}
