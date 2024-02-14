package org.mae.twg.backend.utils.controllerAdvices;

import jakarta.validation.ValidationException;
import org.mae.twg.backend.exceptions.TokenValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(value = TokenValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleValidationException(ValidationException ex, WebRequest request) {
//        TODO: запись в логи

        return ex.getMessage();
    }



}
