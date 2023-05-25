package com.coursemanagement.exeption;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler(value = SystemException.class)
    public ResponseEntity<ApiErrorDto> handleSystemException(final SystemException e, final HttpServletRequest request) {
        log.warn(e.getClass().getSimpleName(), e);
        final SystemErrorCode systemErrorCode = e.getErrorCode();
        final int codeValue = systemErrorCode.getValue();
        final HttpStatus status = HttpStatus.valueOf(codeValue);
        final ApiErrorDto apiErrorDto = new ApiErrorDto(e, status, request);
        return ResponseEntity.status(apiErrorDto.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiErrorDto);
    }

    @ExceptionHandler(value = {BadCredentialsException.class, AccessDeniedException.class})
    public ResponseEntity<ApiErrorDto> handleAccessException(final BadCredentialsException e,
                                                             final HttpServletRequest request) {
        log.warn(e.getClass().getSimpleName(), e);
        final ApiErrorDto apiErrorDto = new ApiErrorDto(e, HttpStatus.FORBIDDEN, request);
        return ResponseEntity.status(apiErrorDto.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiErrorDto);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiErrorDto> handleException(final Exception e, final HttpServletRequest request) {
        log.warn(e.getClass().getSimpleName(), e);
        final ApiErrorDto apiErrorDto = new ApiErrorDto(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.status(apiErrorDto.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiErrorDto);
    }
}
