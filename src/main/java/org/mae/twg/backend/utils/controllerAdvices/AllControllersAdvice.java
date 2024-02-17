package org.mae.twg.backend.utils.controllerAdvices;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class AllControllersAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllControllersAdvice.class);

    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException ex, WebRequest request) {
//        TODO: запись в логи
        LOGGER.error(ex.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
