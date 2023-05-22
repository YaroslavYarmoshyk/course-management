package com.coursemanagement.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(final String token);

    String generateToken(final UserDetails user);

    boolean isTokenValid(final String token, final UserDetails userDetails);
}
