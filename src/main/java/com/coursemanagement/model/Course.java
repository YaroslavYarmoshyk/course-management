package com.coursemanagement.model;

import lombok.Data;

import java.util.Set;

@Data
public class Course {
    private Long code;
    private String title;
    private String description;
    private Set<User> users;
}
