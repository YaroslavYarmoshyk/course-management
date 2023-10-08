package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.service.UserService;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_SUBJECT;
import static com.coursemanagement.util.Constants.REGISTRATION_ENDPOINT;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
public class RegistrationTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private UserService userService;
    @RegisterExtension
    public static final GreenMailExtension GREEN_MAIL = new GreenMailExtension(ServerSetup.SMTP);

    @BeforeAll
    static void beforeAll() {
        RestAssured.filters(new AllureRestAssured());
    }

    @TestFactory
    @DisplayName("Test user registration flow")
    Stream<DynamicTest> testRegistrationFlow() {
        final AuthenticationRequest validAuthRequest = new AuthenticationRequest(FIRST_STUDENT);
        final AuthenticationRequest emptyEmailAuthRequest = new AuthenticationRequest(new User().setPassword("strongPassword"));
        final AuthenticationRequest invalidEmailAuthRequest = new AuthenticationRequest(new User().setEmail("invalidEmail").setPassword("strongPassword"));
        final AuthenticationRequest emptyPassAuthRequest = new AuthenticationRequest(new User().setEmail("invalidEmail"));

        return Stream.of(
                dynamicTest("Test user is not registered before registration", () -> assertThrows(SystemException.class, () -> userService.getUserByEmail(validAuthRequest.email()))),
                dynamicTest("Test successful user registration", () -> testSuccessfulUserRegistration(validAuthRequest)),
                dynamicTest("Test user registration with empty email", () -> testFailureUserRegistration(emptyEmailAuthRequest)),
                dynamicTest("Test user registration with invalid email", () -> testFailureUserRegistration(invalidEmailAuthRequest)),
                dynamicTest("Test user registration with empty password", () -> testFailureUserRegistration(emptyPassAuthRequest)),
                dynamicTest("Test user registration with already existing email", () -> testFailureUserRegistration(validAuthRequest))

        );
    }

    void testSuccessfulUserRegistration(final AuthenticationRequest authenticationRequest) throws Exception {
        final String userEmail = authenticationRequest.email();
        given(requestSpecification)
                .when()
                .body(authenticationRequest)
                .post(REGISTRATION_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/authenticationResponseSchema.json"));
        validateEmailConfirmationSending(userEmail);
        assertNotNull(userService.getUserByEmail(userEmail));
    }

    void validateEmailConfirmationSending(final String email) throws Exception {
        final MimeMessage[] receivedMessages = GREEN_MAIL.getReceivedMessages();
        final MimeMessage receivedMessage = receivedMessages[0];
        assertEquals(1, receivedMessages.length);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(email, receivedMessage.getAllRecipients()[0].toString());
        assertEquals(EMAIL_CONFIRMATION_SUBJECT, receivedMessage.getSubject());
    }

    void testFailureUserRegistration(final AuthenticationRequest authenticationRequest) {
        given(requestSpecification)
                .when()
                .body(authenticationRequest)
                .post(REGISTRATION_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
