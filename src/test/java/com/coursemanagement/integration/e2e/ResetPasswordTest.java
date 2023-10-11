package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.RESET_PASSWORD_CONFIRMATION_ENDPOINT;
import static com.coursemanagement.util.BaseEndpoints.RESET_PASSWORD_REQUEST_ENDPOINT;
import static com.coursemanagement.util.Constants.*;
import static com.coursemanagement.util.MessageUtils.getFirstReceivedMimeMessage;
import static com.coursemanagement.util.MessageUtils.getTokenFromMessage;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
public class ResetPasswordTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private UserService userService;
    @RegisterExtension
    public static final GreenMailExtension GREEN_MAIL_RESET_PASSWORD = new GreenMailExtension(ServerSetup.SMTP);

    @TestFactory
    @DisplayName("Test reset password flow")
    Stream<DynamicTest> testResetPasswordFlow() {
        final String password = "strong-pass";
        final User userWithEmptyEmail = new User().setPassword(password);
        final User userWithInvalidEmail = new User().setEmail("invalid-email").setPassword(password);
        final User userWithEmptyPassword = new User().setEmail(FIRST_STUDENT.getEmail());

        return Stream.of(
                dynamicTest("Test sending request to reset password", this::testResetPasswordRequestSending),
                dynamicTest("Test confirmation token creation", this::testConfirmationTokenCreation),
                dynamicTest("Test reset password request confirmation", this::testResetPasswordRequestConfirmation),
                dynamicTest("Test successful password resetting", this::testSuccessfulPasswordResetting),
                dynamicTest("Test empty email request", () -> testFailureResetPassword(new AuthenticationRequest(userWithEmptyEmail))),
                dynamicTest("Test invalid email request", () -> testFailureResetPassword(new AuthenticationRequest(userWithInvalidEmail))),
                dynamicTest("Test empty password request", () -> testFailureResetPassword(new AuthenticationRequest(userWithEmptyPassword)))
        );
    }

    private void testResetPasswordRequestSending() throws Exception {
        final String email = FIRST_STUDENT.getEmail();
        final String response = given(requestSpecification)
                .when()
                .body(email)
                .post(RESET_PASSWORD_REQUEST_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .extract()
                .response()
                .asPrettyString();
        final MimeMessage receivedMessage = getFirstReceivedMimeMessage(GREEN_MAIL_RESET_PASSWORD);

        assertEquals("Reset password email request was successfully sent", response);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(email, receivedMessage.getAllRecipients()[0].toString());
        assertEquals(RESET_PASSWORD_CONFIRMATION_SUBJECT, receivedMessage.getSubject());

    }

    private void testConfirmationTokenCreation() throws Exception {
        final MimeMessage receivedMessage = getFirstReceivedMimeMessage(GREEN_MAIL_RESET_PASSWORD);
        final String confirmationToken = URLEncoder.encode(getTokenFromMessage(receivedMessage), StandardCharsets.UTF_8);
        final var tokenEntity = confirmationTokenRepository.findByTokenAndType(confirmationToken, TokenType.RESET_PASSWORD);

        assumeTrue(tokenEntity.isPresent());
        assertEquals(TokenStatus.NOT_ACTIVATED, tokenEntity.get().getStatus());
    }

    private void testResetPasswordRequestConfirmation() throws Exception {
        final MimeMessage receivedMessage = getFirstReceivedMimeMessage(GREEN_MAIL_RESET_PASSWORD);
        final String confirmationToken = getTokenFromMessage(receivedMessage);

        final String response = given(requestSpecification)
                .when()
                .get(RESET_PASSWORD_CONFIRMATION_ENDPOINT + "?token=" + confirmationToken)
                .then()
                .spec(validResponseSpecification)
                .extract()
                .response()
                .asPrettyString();

        final var tokenEntity = confirmationTokenRepository.findByTokenAndType(
                URLEncoder.encode(confirmationToken, StandardCharsets.UTF_8),
                TokenType.RESET_PASSWORD
        );

        assertEquals("Reset password request was successfully confirmed", response);
        assumeTrue(tokenEntity.isPresent());
        assertEquals(TokenStatus.ACTIVATED, tokenEntity.get().getStatus());
    }

    private void testSuccessfulPasswordResetting() {
        final User userBeforeUpdate = userService.getUserByEmail(FIRST_STUDENT.getEmail());
        final String oldPassword = "passw@rd-3";
        final String newPassword = "new-strong-passw0rd!";
        final var requestWithOldPass = new AuthenticationRequest(userBeforeUpdate.setPassword(oldPassword));
        final var requestWithNewPass = new AuthenticationRequest(
                userBeforeUpdate.getFirstName(),
                userBeforeUpdate.getLastName(),
                userBeforeUpdate.getEmail(),
                newPassword,
                userBeforeUpdate.getPhone()
        );

        successfulLoginWithOldPasswordStep(requestWithOldPass);
        failureLoginWithNewPasswordBeforeResettingStep(requestWithNewPass);
        successfulResettingPasswordStep(requestWithNewPass);
        successfulLoginWithNewPasswordAfterResettingStep(requestWithNewPass);
    }

    private void testFailureResetPassword(final AuthenticationRequest request) {
        given(requestSpecification)
                .when()
                .body(request)
                .post(RESET_PASSWORD_REQUEST_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void successfulLoginWithOldPasswordStep(final AuthenticationRequest requestWithOldPass) {
        given(requestSpecification)
                .when()
                .body(requestWithOldPass)
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/authenticationResponseSchema.json"));
    }

    private void failureLoginWithNewPasswordBeforeResettingStep(final AuthenticationRequest requestWithNewPass) {
        given(requestSpecification)
                .when()
                .body(requestWithNewPass)
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void successfulResettingPasswordStep(final AuthenticationRequest requestWithNewPass) {
        given(requestSpecification)
                .when()
                .body(requestWithNewPass)
                .post(RESET_PASSWORD_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/authenticationResponseSchema.json"));
    }

    private void successfulLoginWithNewPasswordAfterResettingStep(final AuthenticationRequest requestWithOldPass) {
        given(requestSpecification)
                .when()
                .body(requestWithOldPass)
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/authenticationResponseSchema.json"));
    }
}
