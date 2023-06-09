package com.coursemanagement.model;

import com.coursemanagement.enumeration.UserCourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCourse {
    private Long id;
    private User user;
    private Course course;
    private UserCourseStatus userCourseStatus;
}
