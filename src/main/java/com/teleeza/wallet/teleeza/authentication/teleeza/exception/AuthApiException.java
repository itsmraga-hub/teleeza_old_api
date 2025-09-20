package com.teleeza.wallet.teleeza.authentication.teleeza.exception;

import org.springframework.http.HttpStatus;

public class AuthApiException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public AuthApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public AuthApiException(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
