package com.coursemanagement.exeption;

import com.coursemanagement.exeption.dto.ErrorDto;
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
    public ResponseEntity<ErrorDto> handleSystemException(final SystemException e, final HttpServletRequest request) {
        log.warn(e.getClass().getSimpleName(), e);
        final ErrorDto errorDto = new ErrorDto(e, request);
        return ResponseEntity.status(errorDto.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorDto(e, request));
    }

    @ExceptionHandler(value = {BadCredentialsException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorDto> handleAccessException(final Exception e,
                                                          final HttpServletRequest request) {
        log.warn(e.getClass().getSimpleName(), e);
        final ErrorDto errorDto = new ErrorDto(e, HttpStatus.FORBIDDEN, request);
        return ResponseEntity.status(errorDto.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }
}
