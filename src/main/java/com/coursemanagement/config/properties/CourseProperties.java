package com.coursemanagement.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Data
@ConfigurationProperties(prefix = "course-management")
public class CourseProperties {
    private final String baseUrl;
    private final int studentCourseLimit;
    private final BigDecimal coursePassingPercentage;
}
