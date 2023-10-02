package com.coursemanagement.unit.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.security.controller.ResetPasswordController;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.ResetPasswordService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.stream.Stream;

import static com.coursemanagement.util.ResponseBodyMatcherUtils.responseBody;
import static com.coursemanagement.util.Constants.RESET_PASSWORD_ENDPOINT;
import static com.coursemanagement.util.MvcUtil.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredResourceTest(value = ResetPasswordController.class)
class ResetPasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ResetPasswordService resetPasswordService;
    @MockBean
    private ConfirmationTokenService confirmationTokenService;

    private static final String RESET_PASSWORD_REQUEST_ENDPOINT = RESET_PASSWORD_ENDPOINT + "/request";
    private static final String RESET_PASSWORD_CONFIRMATION_ENDPOINT = RESET_PASSWORD_ENDPOINT + "/confirm";
    private static final String VALID_TOKEN = "valid-token";

    @Order(1)
    @Test
    @DisplayName("Test reset password request endpoint with valid parameter")
    void testResetPasswordRequestEndpoint_ValidParameter() throws Exception {
        final String email = FIRST_STUDENT.getEmail();

        doNothing().when(resetPasswordService).sendResetConfirmation(email);

        makeMockMvcRequest(mockMvc, POST, RESET_PASSWORD_REQUEST_ENDPOINT, email)
                .andExpect(status().isOk())
                .andExpect(responseBody().equalToString("Reset password email request was successfully sent"));
    }

    @Order(2)
    @Test
    @DisplayName("Test reset password confirm endpoint with valid parameter")
    void testResetPasswordConfirmEndpoint_ValidParameter() throws Exception {
        makeMockMvcRequest(mockMvc, GET, RESET_PASSWORD_CONFIRMATION_ENDPOINT, Map.of("token", VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(responseBody().equalToString("Reset password request was successfully confirmed"));

        verify(confirmationTokenService, atMostOnce()).confirmToken(VALID_TOKEN, TokenType.RESET_PASSWORD);
    }

    @Order(3)
    @Test
    @DisplayName("Test reset password endpoint with valid request")
    void testResetPasswordEndpoint_ValidRequest() throws Exception {
        final AuthenticationRequest authenticationRequest = Instancio.of(AuthenticationRequest.class)
                .set(field(AuthenticationRequest::email), FIRST_STUDENT.getEmail())
                .create();
        final AuthenticationResponse authenticationResponse = Instancio.create(AuthenticationResponse.class);

        when(resetPasswordService.resetPassword(authenticationRequest)).thenReturn(authenticationResponse);

        makeMockMvcRequest(mockMvc, POST, RESET_PASSWORD_ENDPOINT, authenticationRequest)
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(authenticationResponse, AuthenticationResponse.class));
    }

    @Order(4)
    @TestFactory
    @DisplayName("Test reset password confirmation endpoints with invalid requests")
    Stream<DynamicTest> testResetPasswordEndpoints_InvalidRequest() {
        return Stream.of(
                dynamicTest("Test reset password request endpoint without email parameter",
                        () -> testMissingParameterRequest(POST, RESET_PASSWORD_REQUEST_ENDPOINT)),
                dynamicTest("Test reset password confirm endpoint without token parameter",
                        () -> testMissingParameterRequest(GET, RESET_PASSWORD_CONFIRMATION_ENDPOINT)),
                dynamicTest("Test reset password endpoint without request body",
                        () -> testMissingParameterRequest(POST, RESET_PASSWORD_ENDPOINT)),
                dynamicTest("Test reset password endpoint without email",
                        () -> testInvalidResetPassRequest(Instancio.of(AuthenticationRequest.class).ignore(field(AuthenticationRequest::email)).create())),
                dynamicTest("Test reset password endpoint without password",
                        () -> testInvalidResetPassRequest(Instancio.of(AuthenticationRequest.class).ignore(field(AuthenticationRequest::password)).create()))
        );
    }

    void testMissingParameterRequest(final HttpMethod httpMethod, final String endpointUrl) throws Exception {
        makeMockMvcRequest(mockMvc, httpMethod, endpointUrl)
                .andExpect(status().isBadRequest());
    }

    void testInvalidResetPassRequest(final AuthenticationRequest authenticationRequest) throws Exception {
        makeMockMvcRequest(mockMvc, POST, RESET_PASSWORD_ENDPOINT, authenticationRequest)
                .andExpect(status().isBadRequest());
    }
}