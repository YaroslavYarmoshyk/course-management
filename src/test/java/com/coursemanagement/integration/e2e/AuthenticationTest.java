package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.collections4.CollectionUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static com.coursemanagement.util.Constants.LOGIN_ENDPOINT;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.RestAssured.given;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
@Sql("/scripts/add_users.sql")
public class AuthenticationTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;

    @TestFactory
    @DisplayName("Test user authentication via login flow")
    Stream<DynamicTest> testUserAuthentication() {
        final AuthenticationRequest authRequestWithoutEmail = Instancio.of(AuthenticationRequest.class)
                .ignore(field(AuthenticationRequest::email))
                .create();
        final AuthenticationRequest authRequestWithInvalidEmail = Instancio.of(AuthenticationRequest.class)
                .set(field(AuthenticationRequest::email), "invalidEmail")
                .create();
        final AuthenticationRequest authRequestWithoutPassword = Instancio.of(AuthenticationRequest.class)
                .ignore(field(AuthenticationRequest::password))
                .set(field(AuthenticationRequest::email), FIRST_STUDENT.getEmail())
                .create();
        final AuthenticationRequest authRequestWithInvalidPassword = Instancio.of(AuthenticationRequest.class)
                .set(field(AuthenticationRequest::password), "invalidPassword")
                .set(field(AuthenticationRequest::email), FIRST_STUDENT.getEmail())
                .create();
        return Stream.of(
                dynamicTest("Test successful admin login", () -> testSuccessfulUserLogin(ADMIN)),
                dynamicTest("Test successful instructor login", () -> testSuccessfulUserLogin(INSTRUCTOR)),
                dynamicTest("Test successful student login", () -> testSuccessfulUserLogin(FIRST_STUDENT)),
                dynamicTest("Test user login without email", () -> testFailureUserLogin(authRequestWithoutEmail, HttpStatus.BAD_REQUEST)),
                dynamicTest("Test user login with invalid email", () -> testFailureUserLogin(authRequestWithInvalidEmail, HttpStatus.BAD_REQUEST)),
                dynamicTest("Test user login without password", () -> testFailureUserLogin(authRequestWithoutPassword, HttpStatus.BAD_REQUEST)),
                dynamicTest("Test user login with invalid password", () -> testFailureUserLogin(authRequestWithInvalidPassword, HttpStatus.FORBIDDEN))
        );
    }

    private void testSuccessfulUserLogin(final User user) {
        final AuthenticationResponse response = given(requestSpecification)
                .when()
                .body(new AuthenticationRequest(user))
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .extract()
                .response()
                .as(AuthenticationResponse.class);
        final String jwt = response.token();

        final User loggedInUser = given(requestSpecification)
                .header("Authorization", "Bearer " + jwt)
                .get("/api/v1/users/me")
                .then()
                .spec(validResponseSpecification)
                .extract()
                .response()
                .as(User.class);
        assertEquals(user.getId(), loggedInUser.getId());
        assertEquals(user.getEmail(), loggedInUser.getEmail());
        assertTrue(CollectionUtils.isEqualCollection(user.getRoles(), loggedInUser.getRoles()));
    }

    void testFailureUserLogin(final AuthenticationRequest authenticationRequest, final HttpStatus expectedStatus) {
        given(requestSpecification)
                .when()
                .body(authenticationRequest)
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(expectedStatus.value());
    }
}
