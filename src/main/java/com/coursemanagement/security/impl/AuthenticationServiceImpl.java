package com.coursemanagement.security.impl;

import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.security.AuthenticationService;
import com.coursemanagement.security.JwtService;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EmailService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthenticationResponse register(final AuthenticationRequest authenticationRequest) {
        final String email = authenticationRequest.email();
        validateAuthenticationRequest(authenticationRequest);
        checkIfEmailIsTaken(email);
        var user = new User()
                .setFirstName(authenticationRequest.firstName())
                .setLastName(authenticationRequest.lastName())
                .setEmail(email)
                .setPhone(authenticationRequest.phone())
                .setStatus(UserStatus.INACTIVE)
                .setPassword(passwordEncoder.encode(authenticationRequest.password()));
        final User savedUser = userService.save(user);
        final String token = jwtService.generateToken(savedUser);
        final ConfirmationToken emailConfirmationToken = confirmationTokenService.createEmailConfirmationToken(savedUser);
        emailService.sendEmailConfirmation(savedUser, emailConfirmationToken.token());
        return new AuthenticationResponse(token);
    }

    @Override
    public AuthenticationResponse verify(final AuthenticationRequest authenticationRequest) {
        validateAuthenticationRequest(authenticationRequest);
        var authenticationToken = getAuthenticationToken(authenticationRequest);
        var authentication = authenticationManager.authenticate(authenticationToken);
        var user = (User) authentication.getPrincipal();
        var token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    private void validateAuthenticationRequest(final AuthenticationRequest authenticationRequest) {
        final String email = authenticationRequest.email();
        final String password = authenticationRequest.password();
        if (Strings.isBlank(email)) {
            throw new SystemException("Email cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (Strings.isBlank(password)) {
            throw new SystemException("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkIfEmailIsTaken(final String email) {
        if (userService.isEmailAlreadyRegistered(email)) {
            throw new SystemException("User with email " + email + " already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private static UsernamePasswordAuthenticationToken getAuthenticationToken(final AuthenticationRequest authenticationRequest) {
        return new UsernamePasswordAuthenticationToken(
                authenticationRequest.email(),
                authenticationRequest.password()
        );
    }
}
