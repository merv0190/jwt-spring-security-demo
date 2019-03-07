package com.example.jwtoken.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJWTokenException extends AuthenticationException {
    public InvalidJWTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidJWTokenException(String msg) {
        super(msg);
    }
}
