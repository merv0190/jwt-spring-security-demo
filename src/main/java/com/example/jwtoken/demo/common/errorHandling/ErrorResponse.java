package com.example.jwtoken.demo.common.errorHandling;

import com.example.jwtoken.demo.enumeration.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    // HTTP Response Status Code
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final ErrorCode errorCode;

    @JsonIgnore
    private final LocalDateTime timestamp;

    private ErrorResponse(final String message, final ErrorCode errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(final String message, final ErrorCode errorCode, HttpStatus status) {
        return new ErrorResponse(message, errorCode, status);
    }

    public Integer getStatus() {
        return this.status.value();
    }
}