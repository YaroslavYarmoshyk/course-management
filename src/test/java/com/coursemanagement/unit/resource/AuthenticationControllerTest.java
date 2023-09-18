package com.coursemanagement.unit.resource;

import com.coursemanagement.security.controller.AuthenticationController;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.security.service.AuthenticationService;
import com.coursemanagement.service.ConfirmationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.coursemanagement.util.TestDataUtils.asJsonString;
import static com.coursemanagement.util.TestDataUtils.getAuthenticationRequest;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthenticationController.class)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private ConfirmationTokenService confirmationTokenService;

    private static final String TEST_AUTHENTICATION_TOKEN = "testEncodedToken";
    private static final AuthenticationRequest AUTHENTICATION_REQUEST = getAuthenticationRequest();
    private static final AuthenticationResponse AUTHENTICATION_RESPONSE = new AuthenticationResponse(TEST_AUTHENTICATION_TOKEN);

    @Test
    @WithMockUser(value = "spring")
    @DisplayName("Test registration endpoint")
    void testRegistrationEndpoint() throws Exception {
        final var requestBuilder = post("").contentType(MediaType.APPLICATION_JSON).content(asJsonString(AUTHENTICATION_REQUEST));

        when(authenticationService.register(AUTHENTICATION_REQUEST)).thenReturn(AUTHENTICATION_RESPONSE);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_AUTHENTICATION_TOKEN));

    }
}