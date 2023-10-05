package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestClientException;

import static com.coursemanagement.util.Constants.REGISTRATION_ENDPOINT;
import static com.coursemanagement.util.Constants.USERS_ENDPOINT;
import static com.coursemanagement.util.MvcUtil.makeCall;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@IntegrationTest
public class RegistrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Test user registration")
    void testUserRegistration() {
        assertThrows(RestClientException.class, () -> makeCall(testRestTemplate, GET, USERS_ENDPOINT + "/me", User.class, FIRST_STUDENT));
        final AuthenticationRequest request = new AuthenticationRequest(FIRST_STUDENT);

        makeCall(testRestTemplate, POST, REGISTRATION_ENDPOINT, request, AuthenticationResponse.class);

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
}
