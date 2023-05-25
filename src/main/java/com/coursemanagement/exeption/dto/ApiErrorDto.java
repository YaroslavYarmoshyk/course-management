package com.coursemanagement.exeption.dto;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Data
public class ApiErrorDto {
    private HttpStatus httpStatus;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now(DEFAULT_ZONE_ID);

    public ApiErrorDto(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ApiErrorDto(final SystemException systemException) {
        final SystemErrorCode systemErrorCode = systemException.getErrorCode();
        this.httpStatus = HttpStatus.valueOf(systemErrorCode.getValue());
        this.message = Optional.ofNullable(systemException.getMessage())
                .orElse(systemErrorCode.getText());
    }
}
