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
    @ExceptionHandler(value = {
            TokenValidationException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseError responseAuthError(RuntimeException exception)  {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }
    @ExceptionHandler(value = {ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError responseErrorNotFoundException(ObjectNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.NOT_FOUND, exception.getMessage());
    }
    @ExceptionHandler(value = {
            ObjectAlreadyExistsException.class,
            ValidationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError responseValidationException(RuntimeException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
    @ExceptionHandler(value = {
            RuntimeException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError responseUnhandledException(RuntimeException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}
