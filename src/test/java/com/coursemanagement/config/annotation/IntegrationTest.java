package com.coursemanagement.config.annotation;


import com.coursemanagement.config.RestAssuredConfiguration;
import com.coursemanagement.config.extension.AllureRestAssuredExtension;
import com.coursemanagement.config.extension.DatabaseSetupExtension;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@ExtendWith({DatabaseSetupExtension.class, AllureRestAssuredExtension.class, InstancioExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RestAssuredConfiguration.class)
@Sql("/scripts/add_users.sql")
public @interface IntegrationTest {
}
