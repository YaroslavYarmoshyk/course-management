package com.coursemanagement.model;

import lombok.Data;

@Data
public class UserCourse {
    private Long id;
    private Long userId;
    private Long courseCode;
    private boolean finished;
}
