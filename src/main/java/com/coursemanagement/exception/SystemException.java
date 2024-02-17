package com.coursemanagement.exception;

import com.coursemanagement.exception.enumeration.SystemErrorCode;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {
    private final SystemErrorCode errorCode;

    public SystemException(final String message, final SystemErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
