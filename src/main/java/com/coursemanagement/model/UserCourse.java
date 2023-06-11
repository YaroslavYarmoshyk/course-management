package com.coursemanagement.model;

import com.coursemanagement.enumeration.UserCourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCourse {
    private Long id;
    private User user;
    private Course course;
    private UserCourseStatus status;
}
