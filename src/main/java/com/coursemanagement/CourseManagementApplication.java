package com.coursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CourseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagementApplication.class, args);
    }

}
