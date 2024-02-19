package org.mae.twg.backend.utils.controllerAdvices;

import jakarta.validation.ValidationException;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AllControllersAdvice {
    private static final Logger LOG = LoggerFactory.getLogger(AllControllersAdvice.class);

    @ExceptionHandler(value = {
            ValidationException.class,
            ObjectAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(RuntimeException ex, WebRequest request) {
//        TODO: запись в логи
        LOG.error(ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(value = ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleObjectNotFoundException(ObjectNotFoundException ex, WebRequest request) {
//        TODO: запись в логи
        LOG.error(ex.getMessage());
        return ex.getMessage();
    }


}
