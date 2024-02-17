package com.coursemanagement.security.controller;

import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.security.service.AuthenticationService;
import com.coursemanagement.service.ConfirmationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.coursemanagement.util.Constants.*;

@RestController
@RequestMapping(value = AUTHENTICATION_ENDPOINT)
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping(value = "/register")
    public AuthenticationResponse register(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return authenticationService.register(authenticationRequest);
    }

    @PostMapping(value = "/login")
    public AuthenticationResponse login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return authenticationService.authenticate(authenticationRequest);
    }

    @GetMapping(value = "/confirm-email")
    public User confirmEmail(@RequestParam(value = TOKEN_CONFIRMATION_ENDPOINT_PARAMETER) final String token) {
        return confirmationTokenService.confirmUserByEmailToken(token);
    }
}
