package com.coursemanagement.config;

import com.coursemanagement.model.User;
import com.coursemanagement.security.service.ApplicationUserDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Objects;
import java.util.stream.Stream;

import static com.coursemanagement.util.TestDataUtils.ADMIN;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static com.coursemanagement.util.TestDataUtils.NEW_USER;
import static com.coursemanagement.util.TestDataUtils.SECOND_STUDENT;

@TestConfiguration
public class SecurityMockConfiguration {

    @Bean
    public UserDetailsService applicationUserDetailsService() {
        return username -> new ApplicationUserDetails(Stream.of(NEW_USER, FIRST_STUDENT, SECOND_STUDENT, INSTRUCTOR, ADMIN)
                .filter(actor -> Objects.equals(username, actor.getEmail()))
                .findFirst()
                .orElse(new User()));
    }
}
