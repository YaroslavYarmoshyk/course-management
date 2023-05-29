package com.coursemanagement.config;

import com.coursemanagement.model.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "users")
public class UserProperties {
    private User admin;
    private User instructor;
    private User student;
}
