package com.coursemanagement.config.properties;

import com.coursemanagement.model.Course;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;

@Data
@TestConfiguration
@ConfigurationProperties(prefix = "courses")
public class CourseTestDataProperties {
    private Course mathematics;
    private Course history;
}
