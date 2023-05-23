package com.coursemanagement.util;

import com.coursemanagement.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class UserUtils {

    public static User resolveCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> (User) authentication.getPrincipal())
                .orElse(null);
    }
}
