package com.coursemanagement.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class SystemException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public SystemException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }
}
