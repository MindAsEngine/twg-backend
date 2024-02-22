package org.mae.twg.backend.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException {
    public ObjectAlreadyExistsException(String msg) {
        super(msg);
    }
}
