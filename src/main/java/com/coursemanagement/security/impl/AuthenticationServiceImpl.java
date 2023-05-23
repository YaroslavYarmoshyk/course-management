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
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public AuthenticationResponse register(final AuthenticationRequest authenticationRequest) {
        final String email = authenticationRequest.email();
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
//        TODO: sent email confirmation token to the user
        final ConfirmationToken emailConfirmationToken = confirmationTokenService.createEmailConfirmationToken(savedUser);
        return new AuthenticationResponse(token);
    }

    private void checkIfEmailIsTaken(final String email) {
        if (userService.isEmailAlreadyRegistered(email)) {
            throw new SystemException("User with email " + email + " already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public AuthenticationResponse authenticate(final AuthenticationRequest authenticationRequest) {
        var authenticationToken = getAuthenticationToken(authenticationRequest);
        var authentication = authenticationManager.authenticate(authenticationToken);
        var user = (User) authentication.getPrincipal();
        var token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    private static UsernamePasswordAuthenticationToken getAuthenticationToken(final AuthenticationRequest authenticationRequest) {
        return new UsernamePasswordAuthenticationToken(
                authenticationRequest.email(),
                authenticationRequest.password()
        );
    }
}
