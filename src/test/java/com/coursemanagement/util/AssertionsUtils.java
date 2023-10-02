package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import org.junit.function.ThrowingRunnable;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static com.coursemanagement.util.ResponseBodyMatcherUtils.responseBody;
import static com.coursemanagement.util.MvcUtil.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.ADMIN;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static com.coursemanagement.util.TestDataUtils.NEW_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class AssertionsUtils {

    public static <T extends Throwable> void assertThrowsWithMessage(final ThrowingRunnable throwingRunnable,
                                                                     final Class<T> expectedError,
                                                                     final String expectedMessage) {
        final Throwable throwable = assertThrows(expectedError, throwingRunnable);
        assertEquals(expectedMessage, throwable.getMessage());
    }

    public static void assertUnauthorizedAccess(final MockMvc mockMvc,
                                                final HttpMethod httpMethod,
                                                final String endpointUrl) throws Exception {
        assertUnauthorizedAccess(mockMvc, httpMethod, endpointUrl, null, null);
    }

    public static void assertUnauthorizedAccess(final MockMvc mockMvc,
                                                final HttpMethod httpMethod,
                                                final String endpointUrl,
                                                final Role atLeastAllowedRole) throws Exception {
        assertUnauthorizedAccess(mockMvc, httpMethod, endpointUrl, null, atLeastAllowedRole);
    }

    public static void assertUnauthorizedAccess(final MockMvc mockMvc,
                                                final HttpMethod httpMethod,
                                                final String endpointUrl,
                                                final Object body) throws Exception {
        assertUnauthorizedAccess(mockMvc, httpMethod, endpointUrl, body, null);
    }

    public static void assertUnauthorizedAccess(final MockMvc mockMvc,
                                                final HttpMethod httpMethod,
                                                final String endpointUrl,
                                                final Object body,
                                                final Role atLeastAllowedRole) throws Exception {
        if (Objects.isNull(atLeastAllowedRole)) {
            makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body).andExpect(status().isUnauthorized());
            return;
        }

        switch (atLeastAllowedRole) {
            case ADMIN -> {
                makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body).andExpect(status().isUnauthorized());
                makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body, FIRST_STUDENT).andExpect(status().isForbidden());
                makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body, INSTRUCTOR).andExpect(status().isForbidden());
            }
            case INSTRUCTOR -> {
                makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body).andExpect(status().isUnauthorized());
                makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body, FIRST_STUDENT).andExpect(status().isForbidden());
            }
            case STUDENT -> {
                makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body, NEW_USER).andExpect(status().isForbidden());
            }
        }
    }

    public static void assertExceptionDeserialization(final MockMvc mockMvc,
                                                      final HttpMethod httpMethod,
                                                      final String endpointUrl,
                                                      final Object methodCall) throws Exception {
        assertExceptionDeserialization(mockMvc, httpMethod, endpointUrl, null, methodCall);
    }

    public static void assertExceptionDeserialization(final MockMvc mockMvc,
                                                      final HttpMethod httpMethod,
                                                      final String endpointUrl,
                                                      final Object body,
                                                      final Object methodCall) throws Exception {
        final SystemException expectedException = new SystemException("Expected exception", SystemErrorCode.BAD_REQUEST);
        when(methodCall).thenThrow(expectedException);
        makeMockMvcRequest(mockMvc, httpMethod, endpointUrl, body, ADMIN)
                .andExpect(responseBody().containsSystemException(expectedException));
    }
}
