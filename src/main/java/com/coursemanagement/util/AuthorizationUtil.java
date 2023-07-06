package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public final class AuthorizationUtil {

    public static boolean isCurrentUserAdminOrInstructor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .map(Role::of)
                .anyMatch(role -> Objects.equals(role, Role.ADMIN) || Objects.equals(role, Role.INSTRUCTOR));
    }

    public static boolean isAdmin(final User user) {
        return userHasRoles(user, Role.ADMIN);
    }

    public static boolean isInstructor(final User user) {
        return userHasRoles(user, Role.INSTRUCTOR);
    }

    private static boolean userHasRoles(final User user, final Role... roles) {
        return Optional.ofNullable(user)
                .map(User::getRoles)
                .map(userRoles -> !Collections.disjoint(userRoles, Arrays.stream(roles).toList()))
                .orElse(false);
    }
}
