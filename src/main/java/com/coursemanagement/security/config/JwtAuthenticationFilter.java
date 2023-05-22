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

import static com.coursemanagement.util.Constants.*;

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
        if (Objects.nonNull(userEmail)) {
            var authenticationToken = getUsernamePasswordAuthenticationToken(request, userEmail);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(final HttpServletRequest request,
                                                                                       final String userEmail) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
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
