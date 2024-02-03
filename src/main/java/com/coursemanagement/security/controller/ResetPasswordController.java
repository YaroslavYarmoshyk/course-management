package com.coursemanagement.security.controller;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.ResetPasswordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.coursemanagement.util.Constants.RESET_PASSWORD_ENDPOINT;
import static com.coursemanagement.util.Constants.TOKEN_CONFIRMATION_ENDPOINT_PARAMETER;

@RestController
@RequestMapping(value = RESET_PASSWORD_ENDPOINT)
@RequiredArgsConstructor
public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping(value = "/request")
    public ResponseEntity<String> sendResetConfirmation(@RequestBody @Email final String email) {
        resetPasswordService.sendResetConfirmation(email);
        return ResponseEntity.ok("Reset password email request was successfully sent");
    }

    @GetMapping(value = "/confirm")
    public ResponseEntity<String> confirmResetting(@RequestParam(value = TOKEN_CONFIRMATION_ENDPOINT_PARAMETER) final String token) {
        final String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        confirmationTokenService.confirmToken(encodedToken, TokenType.RESET_PASSWORD);
        return ResponseEntity.ok("Reset password request was successfully confirmed");
    }

    @PostMapping
    public AuthenticationResponse resetPassword(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return resetPasswordService.resetPassword(authenticationRequest);
    }
}
