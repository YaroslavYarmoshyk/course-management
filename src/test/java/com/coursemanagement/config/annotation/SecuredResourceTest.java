package com.coursemanagement.config.annotation;

import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(value = InstancioExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@WebMvcTest
@EnableSecurityConfiguration
public @interface SecuredResourceTest {

    @AliasFor(annotation = WebMvcTest.class, attribute = "value")
    Class<?>[] value() default {};
}
