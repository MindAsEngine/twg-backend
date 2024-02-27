package org.mae.twg.backend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserNotFound extends AuthenticationException {
    public UserNotFound(String msg) {
        super(msg);
    }
}
