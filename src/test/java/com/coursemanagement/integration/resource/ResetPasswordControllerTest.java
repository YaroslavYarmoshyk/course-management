package com.coursemanagement.integration.resource;

import com.coursemanagement.config.annotation.EnableSecurityConfiguration;
import com.coursemanagement.security.controller.ResetPasswordController;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.ResetPasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ResetPasswordController.class)
@EnableSecurityConfiguration
class ResetPasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ResetPasswordService resetPasswordService;
    @MockBean
    private ConfirmationTokenService confirmationTokenService;

    @Nested
    @DisplayName("Reset password confirmation endpoint tests")
    class ResetPasswordConfirmationEndpointTests {

    }

}