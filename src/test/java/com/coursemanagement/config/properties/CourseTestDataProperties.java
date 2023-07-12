package com.coursemanagement.config.properties;

import com.coursemanagement.model.Course;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "courses")
public class CourseTestDataProperties {
    private Course mathematics;
    private Course history;
}
