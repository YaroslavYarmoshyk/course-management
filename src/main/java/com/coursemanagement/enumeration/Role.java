package com.coursemanagement.enumeration;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;

import java.util.Optional;

public enum Role {
    ADMIN, INSTRUCTOR, STUDENT;

    public static Role of(final String value) {
        return Optional.ofNullable(value)
                .map(stringValue -> stringValue.replace("ROLE_", ""))
                .map(Role::valueOf)
                .orElseThrow(() -> new SystemException("Cannot map role from value: " + value, SystemErrorCode.INTERNAL_SERVER_ERROR));
    }
}
