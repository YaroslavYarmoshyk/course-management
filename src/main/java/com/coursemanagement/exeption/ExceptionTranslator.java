package com.coursemanagement.exeption;

import com.coursemanagement.exeption.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(value = SystemException.class)
    public ApiErrorDto handleSystemException(final HttpServletResponse response, final SystemException e) {
        log.warn(SystemException.class.getSimpleName(), e);
        response.setStatus(e.getErrorCode().getValue());
        return new ApiErrorDto(e);
    }
}
