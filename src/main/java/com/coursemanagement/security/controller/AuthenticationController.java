package com.coursemanagement.security.controller;

import com.coursemanagement.security.service.AuthenticationService;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.coursemanagement.util.Constants.AUTHENTICATION_ENDPOINT;
import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_ENDPOINT;
import static com.coursemanagement.util.Constants.TOKEN_CONFIRMATION_ENDPOINT_PARAMETER;

@RestController
@RequestMapping(value = AUTHENTICATION_ENDPOINT)
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping(value = "/register")
    public AuthenticationResponse register(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationService.register(authenticationRequest);
    }

    @PostMapping(value = "/verify")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationService.verify(authenticationRequest);
    }

    @GetMapping(value = EMAIL_CONFIRMATION_ENDPOINT)
    public void confirmEmail(@RequestParam(value = TOKEN_CONFIRMATION_ENDPOINT_PARAMETER) final String token) {
        userService.confirmUserByEmailToken(token);
    }
}
