package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class AuthorizationUtil {

    public static boolean isCurrentUserAdmin() {
        return currentUserHasRoles(Role.ADMIN);
    }

    public static boolean userHasAnyRole(final User user, final Role... roles) {
        return Optional.ofNullable(user)
                .map(User::getRoles)
                .map(userRoles -> !Collections.disjoint(userRoles, Arrays.stream(roles).toList()))
                .orElse(false);
    }

    private static boolean currentUserHasRoles(final Role... roles) {
        final Set<Role> currentUserRoles = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .map(Role::of)
                .collect(Collectors.toSet());
        return Arrays.stream(roles)
                .allMatch(currentUserRoles::contains);
    }
}
