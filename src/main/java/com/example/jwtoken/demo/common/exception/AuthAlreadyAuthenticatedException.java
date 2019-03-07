package com.example.jwtoken.demo.common.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class AuthAlreadyAuthenticatedException extends AuthenticationServiceException {
    public AuthAlreadyAuthenticatedException(String msg) {
        super(msg);
    }
}
