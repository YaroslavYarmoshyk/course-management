package com.coursemanagement.exeption.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Data
public class ApiErrorDto {
    private HttpStatus status;
    private int code;
    private String path;
    private String message;
    private LocalDateTime timestamp;

    public ApiErrorDto(final Exception exception, final HttpStatus status, final HttpServletRequest request) {
        this.status = status;
        this.code = status.value();
        this.message = exception.getMessage();
        this.path = request.getServletPath();
        this.timestamp = LocalDateTime.now(DEFAULT_ZONE_ID);
    }
}
