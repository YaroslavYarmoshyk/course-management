package com.coursemanagement.exeption;

import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import lombok.Getter;

public class SystemException extends RuntimeException {
    @Getter
    private final SystemErrorCode errorCode;

    public SystemException(final String message, final SystemErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
