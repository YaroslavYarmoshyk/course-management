package com.coursemanagement.security.service;

import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse register(final AuthenticationRequest authenticationRequest);

    AuthenticationResponse authenticate(final AuthenticationRequest authenticationRequest);
}
