package com.coursemanagement.unit.security;

import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.service.JwtService;
import com.coursemanagement.security.service.impl.AuthenticationServiceImpl;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.getAuthenticationRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AuthenticationServiceImplTest {
    @InjectMocks
    @Spy
    private AuthenticationServiceImpl authenticationService;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Spy
    private ModelMapper mapper;

    private static final AuthenticationRequest AUTHENTICATION_REQUEST = getAuthenticationRequest();

    @Order(1)
    @Test
    @DisplayName("Test existing email validation")
    void testEmailAlreadyExist() {
        final String email = AUTHENTICATION_REQUEST.email();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

        assertThrowsWithMessage(
                () -> authenticationService.register(AUTHENTICATION_REQUEST),
                SystemException.class,
                "User with email " + AUTHENTICATION_REQUEST.email() + " already exists"
        );
    }

    @Order(2)
    @Test
    @DisplayName("Test user registration")
    void testUserRegistration() {
        final String email = AUTHENTICATION_REQUEST.email();
        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new UserEntity());
        when(confirmationTokenService.createEmailConfirmationToken(any())).thenReturn(new ConfirmationToken());

        authenticationService.register(AUTHENTICATION_REQUEST);

        verify(userRepository).save(userEntityArgumentCaptor.capture());
        final UserEntity userToSave = userEntityArgumentCaptor.getValue();
        assertEquals(email, userToSave.getEmail());
        assertEquals(AUTHENTICATION_REQUEST.firstName(), userToSave.getFirstName());
        assertEquals(AUTHENTICATION_REQUEST.lastName(), userToSave.getLastName());
        assertEquals(AUTHENTICATION_REQUEST.phone(), userToSave.getPhone());
        assertEquals(UserStatus.INACTIVE, userToSave.getStatus());
        assertNull(userToSave.getRoles());
        assertTrue(passwordEncoder.matches(AUTHENTICATION_REQUEST.password(), userToSave.getPassword()));
        verify(confirmationTokenService).createEmailConfirmationToken(any());
        verify(emailService).sendEmailConfirmation(any(), any());
        verify(authenticationService).authenticate(AUTHENTICATION_REQUEST);
    }

    @Order(3)
    @Test
    @DisplayName("Test authenticate user")
    void testAuthenticateUser() {
        authenticationService.authenticate(AUTHENTICATION_REQUEST);

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateJwt(any());
    }
}