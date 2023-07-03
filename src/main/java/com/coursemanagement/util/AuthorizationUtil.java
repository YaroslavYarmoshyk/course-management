package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class AuthorizationUtil {

    public static boolean isAdmin() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .map(Role::of)
                .anyMatch(role -> Objects.equals(role, Role.ADMIN));
    }

    public static boolean isAdmin(final User user) {
        return Optional.ofNullable(user)
                .map(User::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(role -> Objects.equals(role, Role.ADMIN));
    }

    public static boolean isAdminOrInstructor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .map(Role::of)
                .anyMatch(role -> Objects.equals(role, Role.ADMIN) || Objects.equals(role, Role.INSTRUCTOR));
    }
}
