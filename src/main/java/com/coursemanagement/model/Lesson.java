package com.coursemanagement.model;

import lombok.Data;

@Data
public class Lesson {
    private Long id;
    private Course course;
    private String title;
    private String description;
}
