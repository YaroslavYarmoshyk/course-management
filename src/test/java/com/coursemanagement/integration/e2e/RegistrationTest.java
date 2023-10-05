package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.UserService;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestClientException;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_SUBJECT;
import static com.coursemanagement.util.Constants.REGISTRATION_ENDPOINT;
import static com.coursemanagement.util.Constants.USERS_ENDPOINT;
import static com.coursemanagement.util.MvcUtil.makeCall;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@IntegrationTest
public class RegistrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserService userService;
    @RegisterExtension
    public static final GreenMailExtension GREEN_MAIL = new GreenMailExtension(ServerSetup.SMTP);

    @Test
    @DisplayName("Test user registration")
    void testUserRegistration() throws Exception {
        assertThrows(RestClientException.class, () -> makeCall(testRestTemplate, GET, USERS_ENDPOINT + "/me", User.class, FIRST_STUDENT));
        final AuthenticationRequest request = new AuthenticationRequest(FIRST_STUDENT);

        makeCall(testRestTemplate, POST, REGISTRATION_ENDPOINT, request, AuthenticationResponse.class);

        validateEmailConfirmationSending();

        final User user = userService.getUserByEmail(FIRST_STUDENT.getEmail());
        assertEquals(request.email(), user.getEmail());
        assertEquals(request.firstName(), user.getFirstName());
        assertEquals(request.lastName(), user.getLastName());
        assertEquals(request.phone(), user.getPhone());
        assertTrue(user.getRoles().isEmpty());
        assertEquals(UserStatus.INACTIVE, user.getStatus());

        final User loggedInUser = makeCall(testRestTemplate, GET, USERS_ENDPOINT + "/me", User.class, FIRST_STUDENT);
        assertEquals(user.getId(), loggedInUser.getId());
    }

    void validateEmailConfirmationSending() throws Exception {
        final MimeMessage[] receivedMessages = GREEN_MAIL.getReceivedMessages();
        final MimeMessage receivedMessage = receivedMessages[0];
        assertEquals(1, receivedMessages.length);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(FIRST_STUDENT.getEmail(), receivedMessage.getAllRecipients()[0].toString());
        assertEquals(EMAIL_CONFIRMATION_SUBJECT, receivedMessage.getSubject());
    }
}
