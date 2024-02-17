package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.service.AuthenticationService;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EmailService;
import com.coursemanagement.service.UserService;
import com.coursemanagement.service.impl.ResetPasswordServiceImpl;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class ResetPasswordServiceImplTest {
    @InjectMocks
    private ResetPasswordServiceImpl resetPasswordService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private AuthenticationService authenticationService;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private EmailService emailService;
    @Mock
    private UserService userService;

    private static final String TEST_CONFIRMATION_TOKEN = "test-token";
    private String firstStudentBasePass;

    @BeforeEach
    void saveBasePass() {
        firstStudentBasePass = FIRST_STUDENT.getPassword();
    }

    @AfterEach
    void setBasePass() {
        FIRST_STUDENT.setPassword(firstStudentBasePass);
    }

    @TestFactory
    @DisplayName("Test reset password flows")
    Stream<DynamicTest> testResetPasswordFlows() {
        return Stream.of(
                dynamicTest("Test email validation during password reset confirmation",
                        () -> assertThrows(SystemException.class, () -> resetPasswordService.sendResetConfirmation(Strings.EMPTY))),
                dynamicTest("Test email validation during password reset",
                        () -> assertThrows(SystemException.class, () -> resetPasswordService.resetPassword(prepareAuthenticationRequest(Strings.EMPTY, Strings.EMPTY)))),
                dynamicTest("Test reset password confirmation token creation", this::testSendResetConfirmation),
                dynamicTest("Test reset password", this::testResetPassword)
        );
    }

    void testSendResetConfirmation() {
        final ConfirmationToken confirmationToken = prepareConfirmationToken();

        when(userService.getUserByEmail(FIRST_STUDENT.getEmail())).thenReturn(FIRST_STUDENT);
        when(confirmationTokenService.createResetPasswordToken(FIRST_STUDENT)).thenReturn(confirmationToken);

        resetPasswordService.sendResetConfirmation(FIRST_STUDENT.getEmail());

        verify(emailService).sendResetPasswordConfirmation(eq(FIRST_STUDENT), eq(TEST_CONFIRMATION_TOKEN));
    }

    void testResetPassword() {
        final String oldPassword = FIRST_STUDENT.getPassword();
        final String newPassword = oldPassword + "new";
        final AuthenticationRequest request = prepareAuthenticationRequest(FIRST_STUDENT.getEmail(), newPassword);
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.getUserByEmail(request.email())).thenReturn(FIRST_STUDENT);
        resetPasswordService.resetPassword(request);

        verify(userService).save(userCaptor.capture());
        final User capturedUser = userCaptor.getValue();
        assertTrue(passwordEncoder.matches(newPassword, capturedUser.getPassword()));
    }

    private static ConfirmationToken prepareConfirmationToken() {
        return new ConfirmationToken().setId(1L)
                .setToken(TEST_CONFIRMATION_TOKEN)
                .setType(TokenType.RESET_PASSWORD)
                .setStatus(TokenStatus.NOT_ACTIVATED)
                .setUserId(FIRST_STUDENT.getId());
    }

    private static AuthenticationRequest prepareAuthenticationRequest(final String email, final String newPassword) {
        return new AuthenticationRequest(FIRST_STUDENT.getFirstName(),
                FIRST_STUDENT.getLastName(),
                email,
                FIRST_STUDENT.getPhone(),
                newPassword);
    }
}