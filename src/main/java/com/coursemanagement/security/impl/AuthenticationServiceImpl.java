package com.coursemanagement.security.impl;

import com.coursemanagement.model.User;
import com.coursemanagement.security.AuthenticationService;
import com.coursemanagement.security.JwtService;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(final AuthenticationRequest authenticationRequest) {
        var user = new User()
                .setFirstName(authenticationRequest.firstName())
                .setLastName(authenticationRequest.lastName())
                .setEmail(authenticationRequest.email())
                .setPhone(authenticationRequest.phone())
                .setPassword(passwordEncoder.encode(authenticationRequest.password()));
        userService.save(user);
        final String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
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
