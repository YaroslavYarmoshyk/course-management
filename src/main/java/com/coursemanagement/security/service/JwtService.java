package com.coursemanagement.security.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateJwt(final Authentication authentication);
}
