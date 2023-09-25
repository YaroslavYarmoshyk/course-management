package com.coursemanagement.config.annotation;

import com.coursemanagement.config.SecurityMockConfiguration;
import com.coursemanagement.security.config.ApplicationAccessDeniedHandler;
import com.coursemanagement.security.config.ApplicationAuthenticationEntryPoint;
import com.coursemanagement.security.config.RSAKeyProperties;
import com.coursemanagement.security.config.SecurityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {
        SecurityConfiguration.class,
        SecurityMockConfiguration.class,
        ApplicationAccessDeniedHandler.class,
        ApplicationAuthenticationEntryPoint.class,
        RSAKeyProperties.class
})
public @interface EnableSecurityConfiguration {
}
