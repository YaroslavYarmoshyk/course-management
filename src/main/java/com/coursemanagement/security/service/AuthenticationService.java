package com.coursemanagement.security.service;

import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse register(AuthenticationRequest authenticationRequest);

    AuthenticationResponse verify(AuthenticationRequest authenticationRequest);
}
