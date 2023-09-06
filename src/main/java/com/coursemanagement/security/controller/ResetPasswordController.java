package com.coursemanagement.security.controller;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.coursemanagement.util.Constants.*;

@RestController
@RequestMapping(value = RESET_PASSWORD_ENDPOINT)
@RequiredArgsConstructor
public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping(value = RESET_PASSWORD_CONFIRMATION_ENDPOINT)
    public void sendResetConfirmation(@RequestBody final String email) {
        resetPasswordService.sendResetConfirmation(email);
    }

    @GetMapping(value = RESET_PASSWORD_CONFIRMATION_ENDPOINT)
    public void confirmResetting(@RequestParam(value = TOKEN_CONFIRMATION_ENDPOINT_PARAMETER) final String token) {
        final String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        confirmationTokenService.confirmToken(encodedToken, TokenType.RESET_PASSWORD);
    }

    @PostMapping
    public AuthenticationResponse resetPassword(@RequestBody AuthenticationRequest authenticationRequest) {
        return resetPasswordService.resetPassword(authenticationRequest);
    }
}
