package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.security.JwtService;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EmailService;
import com.coursemanagement.service.ResetPasswordService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public void sendResetConfirmation(final String email) {
        if (Strings.isBlank(email)) {
            throw new SystemException("Email cannot be empty", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
        final User user = userService.findByEmail(email);
        final ConfirmationToken resetPasswordToken = confirmationTokenService.createResetPasswordToken(user);
        emailService.sendResetPasswordConfirmation(user, resetPasswordToken.token());
    }

    @Override
    public AuthenticationResponse resetPassword(final AuthenticationRequest authenticationRequest) {
        final String email = authenticationRequest.email();
        if (Strings.isNotBlank(email)) {
            final User user = userService.findByEmail(email);
            user.setPassword(passwordEncoder.encode(authenticationRequest.password()));
            final User savedUser = userService.save(user);
            final String token = jwtService.generateToken(savedUser);
            return new AuthenticationResponse(token);
        }
        throw new SystemException("Email cannot be empty", SystemErrorCode.INTERNAL_SERVER_ERROR);
    }
}
