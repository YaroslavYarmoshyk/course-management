package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import static com.coursemanagement.util.Constants.AUTHENTICATION_ENDPOINT;
import static com.coursemanagement.util.MvcUtil.makeMockMvcRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class RegistrationFlowTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Test
    void testUserRegistration() throws Exception {
        makeMockMvcRequest(mockMvc, HttpMethod.POST, AUTHENTICATION_ENDPOINT + "/register", new AuthenticationRequest("Yaroslav", "Yarmoshyk", "yarmoshyk96@gmail.com", "+380974309051", "123321aa"));
        final User user = userService.getUserByEmail("yarmoshyk96@gmail.com");
        final User john = userService.getUserByEmail("john-smith@gmail.com");
        assertEquals("yarmoshyk96@gmail.com", user.getEmail());
    }
}
