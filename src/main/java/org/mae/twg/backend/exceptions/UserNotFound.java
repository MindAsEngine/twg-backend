package org.mae.twg.backend.exceptions;

import javax.naming.AuthenticationException;

public class UserNotFound extends AuthenticationException {
    public UserNotFound(String msg) {
        super(msg);
    }
}
