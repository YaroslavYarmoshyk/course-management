package com.coursemanagement.util;

import com.auth0.jwt.JWT;
import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coursemanagement.util.Constants.LOGIN_ENDPOINT;
import static io.restassured.RestAssured.given;


public class JwtTokenUtils {
    private static final Map<String, String> TOKENS = new HashMap<>();

    public static String getTokenForUser(final User user, final RequestSpecification requestSpecification) {
        final String email = user.getEmail();
        final String currentToken = TOKENS.get(email);
        if (tokenExpired(currentToken)) {
            final AuthenticationRequest request = new AuthenticationRequest(user);
            final String newToken = login(request, requestSpecification).token();
            TOKENS.put(email, newToken);
            return newToken;
        }
        return currentToken;
    }

    private static boolean tokenExpired(final String currentToken) {
        return Optional.ofNullable(currentToken)
                .map(token -> JWT.decode(token).getExpiresAt().before(new Date()))
                .orElse(true);
    }

    public static AuthenticationResponse login(final AuthenticationRequest request,
                                               final RequestSpecification requestSpecification) {
        return given(requestSpecification)
                .when()
                .body(request)
                .post(LOGIN_ENDPOINT)
                .as(AuthenticationResponse.class);
    }
}
