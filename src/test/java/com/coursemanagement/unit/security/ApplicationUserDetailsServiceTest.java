package com.coursemanagement.unit.security;

import com.coursemanagement.security.service.impl.ApplicationUserDetailsService;
import com.coursemanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class ApplicationUserDetailsServiceTest {
    @InjectMocks
    private ApplicationUserDetailsService applicationUserDetailsService;
    @Mock
    private UserService userService;

    @Test
    @DisplayName("Test load user by email")
    void testLoadUserByEmail() {
        final String email = FIRST_STUDENT.getEmail();

        when(userService.getUserByEmail(email)).thenReturn(FIRST_STUDENT);

        final UserDetails userDetails = applicationUserDetailsService.loadUserByUsername(FIRST_STUDENT.getEmail());

        verify(userService).getUserByEmail(FIRST_STUDENT.getEmail());
        assertEquals(email, userDetails.getUsername());
    }
}