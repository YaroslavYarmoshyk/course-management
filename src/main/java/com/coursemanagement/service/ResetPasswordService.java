package com.coursemanagement.service;

import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;

public interface ResetPasswordService {

    void sendResetConfirmation(final String email);

    AuthenticationResponse resetPassword(final AuthenticationRequest authenticationRequest);
}
