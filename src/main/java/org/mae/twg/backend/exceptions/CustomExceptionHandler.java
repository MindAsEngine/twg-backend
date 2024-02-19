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
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseError responseErrorValidation(ValidationException exception)  {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseError responseErrorTokenValidation(TokenValidationException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError responseErrorNotFoundException(ObjectNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.NOT_FOUND, exception.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError responseErrorValidationException(RuntimeException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError responseErrorObjectAlreadyExist(ObjectAlreadyExistsException exception) {
        log.error(exception.getMessage());
        return new ResponseError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
