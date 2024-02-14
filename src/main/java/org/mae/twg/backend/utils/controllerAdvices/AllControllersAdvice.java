package org.mae.twg.backend.utils.controllerAdvices;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AllControllersAdvice {
//    private static final Logger LOGGER = LoggerFactory.getLogger(AllControllersAdvice.class);

    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException ex, WebRequest request) {
//        TODO: запись в логи
        return ex.getMessage();
    }
}
