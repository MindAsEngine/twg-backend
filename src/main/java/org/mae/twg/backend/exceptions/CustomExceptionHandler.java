package org.mae.twg.backend.exceptions;

import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public ResponseError responseErrorValidation(ValidationException exception)  {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.I_AM_A_TEAPOT, exception.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public ResponseError responseErrorTokenValidation(TokenValidationException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.I_AM_A_TEAPOT, exception.getMessage());
    }
}
