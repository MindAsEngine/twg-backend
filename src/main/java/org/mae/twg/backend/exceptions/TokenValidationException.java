package org.mae.twg.backend.exceptions;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String msg) {
        super(msg);
    }
}
