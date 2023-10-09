package com.coursemanagement.unit.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.rest.UserResource;
import com.coursemanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.Constants.USERS_ENDPOINT;
import static com.coursemanagement.util.MvcUtils.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

@SecuredResourceTest(value = UserResource.class)
class UserResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @TestFactory
    @DisplayName("Test current user endpoint")
    Stream<DynamicTest> testCurrentUserEndpoint() {
        final String currentUserEndpoint = USERS_ENDPOINT + "/me";
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, currentUserEndpoint)),
                dynamicTest("Test valid current user request",
                        () -> {
                            when(userService.getUserById(FIRST_STUDENT.getId())).thenReturn(FIRST_STUDENT);
                            makeMockMvcRequest(mockMvc, GET, currentUserEndpoint, FIRST_STUDENT);
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                currentUserEndpoint,
                                userService.getUserById(anyLong())
                        )
                )
        );
    }
}