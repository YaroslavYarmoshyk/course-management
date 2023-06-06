package com.coursemanagement.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Course {
    private Long code;
    private String title;
    private String description;
    private Set<User> users;
}
