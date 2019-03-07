package com.example.jwtoken.demo.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    ALREADYAUTHENTICATED(1),
    AUTHENTICATION(2),
    SERVERERROR(98),
    UNKNOWN(100);

    private int errorCode;

    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}
