package com.coursemanagement.config.properties;

import com.coursemanagement.model.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;

@Data
@TestConfiguration
@ConfigurationProperties(prefix = "users")
public class UserTestDataProperties {
    private User admin;
    private User instructor;
    private User student;
    private User newUser;
}
