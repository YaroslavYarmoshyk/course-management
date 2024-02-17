package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.ConfirmationTokenRepository;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.service.UserService;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_SUBJECT;
import static com.coursemanagement.util.Constants.REGISTRATION_ENDPOINT;
import static com.coursemanagement.util.MessageUtils.getFirstReceivedMimeMessage;
import static com.coursemanagement.util.MessageUtils.getTokenFromMessage;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
public class RegistrationTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private UserService userService;
    @RegisterExtension
    private static final GreenMailExtension GREEN_MAIL_REGISTRATION = new GreenMailExtension(ServerSetup.SMTP);

    @Sql("/scripts/remove_users.sql")
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
                dynamicTest("Test user registration with already existing email", () -> testFailureUserRegistration(validAuthRequest)),
                dynamicTest("Test email confirmation receiving", () -> testEmailConfirmationReceiving(validAuthRequest.email())),
                dynamicTest("Test user activation by confirmation token from email", () -> testUserActivationByConfirmationToken(validAuthRequest.email()))
        );
    }

    private void testSuccessfulUserRegistration(final AuthenticationRequest authenticationRequest) {
        final String userEmail = authenticationRequest.email();
        given(requestSpecification)
                .when()
                .body(authenticationRequest)
                .post(REGISTRATION_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/authenticationResponseSchema.json"));
        assertNotNull(userService.getUserByEmail(userEmail));
    }

    private void testFailureUserRegistration(final AuthenticationRequest authenticationRequest) {
        given(requestSpecification)
                .when()
                .body(authenticationRequest)
                .post(REGISTRATION_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void testEmailConfirmationReceiving(final String email) throws Exception {
        final MimeMessage receivedMessage = getFirstReceivedMimeMessage(GREEN_MAIL_REGISTRATION);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(email, receivedMessage.getAllRecipients()[0].toString());
        assertEquals(EMAIL_CONFIRMATION_SUBJECT, receivedMessage.getSubject());
    }

    private void testUserActivationByConfirmationToken(final String email) throws Exception {
        final User userBeforeActivation = userService.getUserByEmail(email);
        assertEquals(UserStatus.INACTIVE, userBeforeActivation.getStatus());

        final MimeMessage firstReceivedMimeMessage = getFirstReceivedMimeMessage(GREEN_MAIL_REGISTRATION);
        final String token = getTokenFromMessage(firstReceivedMimeMessage);
        final var tokenEntity = confirmationTokenRepository.findByTokenAndType(
                URLEncoder.encode(token, StandardCharsets.UTF_8),
                TokenType.EMAIL_CONFIRMATION
        );

        assumeTrue(tokenEntity.isPresent());
        assertEquals(TokenStatus.NOT_ACTIVATED, tokenEntity.get().getStatus());

        given(requestSpecification)
                .when()
                .get("/api/v1/authentication/confirm-email?token=" + token)
                .then()
                .spec(validResponseSpecification);

        final User userAfterConfirmation = userService.getUserByEmail(email);
        final var activatedToken = confirmationTokenRepository.findByTokenAndType(
                URLEncoder.encode(token, StandardCharsets.UTF_8),
                TokenType.EMAIL_CONFIRMATION
        );

        assumeTrue(activatedToken.isPresent());
        assertEquals(TokenStatus.ACTIVATED, activatedToken.get().getStatus());
        assertEquals(UserStatus.ACTIVE, userAfterConfirmation.getStatus());
    }
}
