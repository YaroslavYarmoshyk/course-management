package com.coursemanagement.integration.resource;

import com.coursemanagement.config.annotation.EnableSecurityConfiguration;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.User;
import com.coursemanagement.security.controller.AuthenticationController;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.security.service.AuthenticationService;
import com.coursemanagement.service.ConfirmationTokenService;
import org.apache.logging.log4j.util.Strings;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.stream.Stream;

import static com.coursemanagement.config.ResponseBodyMatchers.responseBody;
import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_ENDPOINT;
import static com.coursemanagement.util.MvcUtil.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.getAuthenticationRequest;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(value = InstancioExtension.class)
@WebMvcTest(controllers = AuthenticationController.class)
@EnableSecurityConfiguration
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private ConfirmationTokenService confirmationTokenService;

    public static final String REGISTRATION_ENDPOINT = "/api/v1/authentication/register";
    private static final String LOGIN_ENDPOINT = "/api/v1/authentication/login";
    private static final String CONFIRMATION_ENDPOINT = "/api/v1/authentication" + EMAIL_CONFIRMATION_ENDPOINT;
    private static final String TEST_AUTHENTICATION_TOKEN = "testEncodedToken";
    private static final AuthenticationRequest AUTHENTICATION_REQUEST = getAuthenticationRequest();
    private static final AuthenticationResponse AUTHENTICATION_RESPONSE = new AuthenticationResponse(TEST_AUTHENTICATION_TOKEN);

    @Nested
    @DisplayName("User registration endpoint tests")
    class RegistrationEndpointTests {

        @Test
        @DisplayName("Test registration endpoint valid request")
        void testRegistrationEndpoint_ValidRequest() throws Exception {
            when(authenticationService.register(AUTHENTICATION_REQUEST)).thenReturn(AUTHENTICATION_RESPONSE);

            makeMockMvcRequest(mockMvc, POST, REGISTRATION_ENDPOINT, AUTHENTICATION_REQUEST)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(TEST_AUTHENTICATION_TOKEN))
                    .andExpect(responseBody().containsObjectAsJson(AUTHENTICATION_RESPONSE, AuthenticationResponse.class));
        }

        @Test
        @DisplayName("Test registration endpoint when email is already taken")
        void testRegistrationEndpoint_AlreadyTakenEmail() throws Exception {
            final SystemException expectedException = new SystemException(
                    "User with email " + AUTHENTICATION_REQUEST.email() + " already exists",
                    SystemErrorCode.BAD_REQUEST
            );

            when(authenticationService.register(AUTHENTICATION_REQUEST)).thenThrow(expectedException);

            makeMockMvcRequest(mockMvc, POST, REGISTRATION_ENDPOINT, AUTHENTICATION_REQUEST)
                    .andExpect(status().isBadRequest())
                    .andExpect(responseBody().containsSystemException(expectedException));
        }
    }

    @Nested
    @DisplayName("User login endpoint tests")
    class LoginEndpointTests {

        @Test
        @DisplayName("Test login endpoint valid request")
        void testLoginEndpoint_ValidRequest() throws Exception {
            when(authenticationService.authenticate(AUTHENTICATION_REQUEST)).thenReturn(AUTHENTICATION_RESPONSE);

            makeMockMvcRequest(mockMvc, POST, LOGIN_ENDPOINT, AUTHENTICATION_REQUEST)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(TEST_AUTHENTICATION_TOKEN))
                    .andExpect(responseBody().containsObjectAsJson(AUTHENTICATION_RESPONSE, AuthenticationResponse.class));
        }

        @Test
        @DisplayName("Test login endpoint with bad credentials")
        void testLoginEndpoint_BadCredentials() throws Exception {
            final BadCredentialsException expectedException = new BadCredentialsException("Bad credentials");

            when(authenticationService.authenticate(AUTHENTICATION_REQUEST)).thenThrow(expectedException);

            makeMockMvcRequest(mockMvc, POST, LOGIN_ENDPOINT, AUTHENTICATION_REQUEST)
                    .andExpect(status().isForbidden())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException));
        }
    }

    @Nested
    @DisplayName("Email confirmation endpoint tests")
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    class EmailConfirmationEndpointTests {

        @Order(1)
        @Test
        @DisplayName("Test email confirmation endpoint with valid confirmation token")
        void testEmailConfirmationEndpoint_TokenIsPresent() throws Exception {
            when(confirmationTokenService.confirmUserByEmailToken(TEST_AUTHENTICATION_TOKEN)).thenReturn(FIRST_STUDENT);

            makeMockMvcRequest(mockMvc, GET, CONFIRMATION_ENDPOINT, Map.of("token", TEST_AUTHENTICATION_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(responseBody().containsObjectAsJson(FIRST_STUDENT, User.class));

        }

        @Order(2)
        @Test
        @DisplayName("Test email confirmation endpoint with invalid confirmation token")
        void testEmailConfirmationEndpoint_InvalidToken() throws Exception {
            when(confirmationTokenService.confirmUserByEmailToken(TEST_AUTHENTICATION_TOKEN)).thenReturn(FIRST_STUDENT);

            makeMockMvcRequest(mockMvc, GET, CONFIRMATION_ENDPOINT, Map.of("token", TEST_AUTHENTICATION_TOKEN + "invalid"))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertTrue(Strings.isEmpty(result.getResponse().getContentAsString())));

        }

        @Order(3)
        @Test
        @DisplayName("Test email confirmation endpoint without confirmation token")
        void testEmailConfirmationEndpoint_AbsentToken() throws Exception {
            makeMockMvcRequest(mockMvc, GET, CONFIRMATION_ENDPOINT, null)
                    .andExpect(status().isBadRequest());

        }
    }

    @TestFactory
    @DisplayName("Test authentication endpoints with invalid requests")
    Stream<DynamicTest> testRegistrationEndpoint_InvalidRequest() {
        final AuthenticationRequest emptyEmailRequest = Instancio.of(AuthenticationRequest.class)
                .set(field(AuthenticationRequest::email), Strings.EMPTY)
                .create();
        final AuthenticationRequest invalidEmailRequest = Instancio.of(AuthenticationRequest.class)
                .set(field(AuthenticationRequest::email), "invalidEmail")
                .create();
        final AuthenticationRequest nullEmailRequest = Instancio.of(AuthenticationRequest.class)
                .ignore(field(AuthenticationRequest::email))
                .create();
        final AuthenticationRequest emptyPasswordRequest = Instancio.of(AuthenticationRequest.class)
                .set(field(AuthenticationRequest::password), Strings.EMPTY)
                .create();
        final AuthenticationRequest nullPasswordRequest = Instancio.of(AuthenticationRequest.class)
                .ignore(field(AuthenticationRequest::email))
                .create();

        return Stream.of(
                dynamicTest("Test empty email registration request", () -> testInvalidAuthenticationRequest(emptyEmailRequest, REGISTRATION_ENDPOINT)),
                dynamicTest("Test invalid email registration request", () -> testInvalidAuthenticationRequest(invalidEmailRequest, REGISTRATION_ENDPOINT)),
                dynamicTest("Test null email registration request", () -> testInvalidAuthenticationRequest(nullEmailRequest, REGISTRATION_ENDPOINT)),
                dynamicTest("Test empty password registration request", () -> testInvalidAuthenticationRequest(emptyPasswordRequest, REGISTRATION_ENDPOINT)),
                dynamicTest("Test null password registration request", () -> testInvalidAuthenticationRequest(nullPasswordRequest, REGISTRATION_ENDPOINT)),
                dynamicTest("Test empty email login request", () -> testInvalidAuthenticationRequest(emptyEmailRequest, LOGIN_ENDPOINT)),
                dynamicTest("Test invalid email login request", () -> testInvalidAuthenticationRequest(invalidEmailRequest, LOGIN_ENDPOINT)),
                dynamicTest("Test null email login request", () -> testInvalidAuthenticationRequest(nullEmailRequest, LOGIN_ENDPOINT)),
                dynamicTest("Test empty password login request", () -> testInvalidAuthenticationRequest(emptyPasswordRequest, LOGIN_ENDPOINT)),
                dynamicTest("Test null password login request", () -> testInvalidAuthenticationRequest(nullPasswordRequest, LOGIN_ENDPOINT))
        );
    }

    void testInvalidAuthenticationRequest(final AuthenticationRequest authenticationRequest, final String endpointUrl) throws Exception {
        makeMockMvcRequest(mockMvc, POST, endpointUrl, authenticationRequest)
                .andExpect(status().isBadRequest());
    }
}