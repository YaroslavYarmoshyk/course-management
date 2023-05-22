package com.coursemanagement.security.config;

import com.coursemanagement.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static com.coursemanagement.util.Constants.AUTHORIZATION_HEADER;
import static com.coursemanagement.util.Constants.BEARER;
import static com.coursemanagement.util.Constants.BEARER_TOKEN_START_INDEX;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader(AUTHORIZATION_HEADER);
        final boolean isAlreadyAuthenticated = Objects.nonNull(SecurityContextHolder.getContext().getAuthentication());
        if (
                Objects.isNull(authenticationHeader)
                        || !authenticationHeader.startsWith(BEARER)
                        || isAlreadyAuthenticated
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authenticationHeader.substring(BEARER_TOKEN_START_INDEX);
        final String userEmail = jwtService.extractUsername(jwt);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        final boolean tokenIsValid = jwtService.isTokenValid(jwt, userDetails);
        if (tokenIsValid) {
            var authenticationToken = getUsernamePasswordAuthenticationToken(request, userDetails);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(final HttpServletRequest request,
                                                                                       final UserDetails userDetails) {
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        final WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource()
                .buildDetails(request);
        authenticationToken.setDetails(webAuthenticationDetails);
        return authenticationToken;
    }
}
