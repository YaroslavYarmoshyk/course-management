package com.coursemanagement.config;

import com.coursemanagement.security.service.impl.ApplicationUserDetailsService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityMockConfiguration {

    @MockBean
    private ApplicationUserDetailsService userDetailsService;
}
