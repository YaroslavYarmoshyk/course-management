package com.coursemanagement.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.lessThan;

@Lazy
@TestConfiguration
public class RestAssuredConfiguration {
    @LocalServerPort
    private int port;

    @Bean
    public RequestSpecification requestSpecification() {
        return new RequestSpecBuilder().setBaseUri("http://localhost")
                .setContentType(ContentType.JSON)
                .setPort(port)
                .build();
    }

    @Bean
    public ResponseSpecification validResponseSpecification() {
        return new ResponseSpecBuilder().expectStatusCode(HttpStatus.OK.value())
                .expectResponseTime(lessThan(5000L))
                .build();
    }
}
