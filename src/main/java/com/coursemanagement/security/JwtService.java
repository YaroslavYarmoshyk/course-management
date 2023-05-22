package com.coursemanagement.security;

public interface JwtService {
    String extractUsername(final String token);
}
