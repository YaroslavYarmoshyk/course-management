package com.coursemanagement.exception.dto;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;
import static com.coursemanagement.util.DateTimeUtils.ERROR_DATE_FORMAT_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
    @JsonFormat(pattern = ERROR_DATE_FORMAT_PATTERN)
    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;


    public ErrorDto(final Exception exception, final HttpStatus status, final HttpServletRequest request) {
        this.timestamp = Date.from(LocalDateTime.now(DEFAULT_ZONE_ID).toInstant(ZoneOffset.UTC));
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = exception.getMessage();
        this.path = Strings.isBlank(request.getServletPath()) ? request.getPathInfo() : request.getServletPath();
    }

    public ErrorDto(final SystemException systemException, final HttpServletRequest request) {
        final SystemErrorCode systemErrorCode = systemException.getErrorCode();
        final int codeValue = systemErrorCode.getValue();
        final HttpStatus status = HttpStatus.valueOf(codeValue);
        this.timestamp = Date.from(LocalDateTime.now(DEFAULT_ZONE_ID).toInstant(ZoneOffset.UTC));
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = systemException.getMessage();
        this.path = Strings.isBlank(request.getServletPath()) ? request.getPathInfo() : request.getServletPath();
    }
}
