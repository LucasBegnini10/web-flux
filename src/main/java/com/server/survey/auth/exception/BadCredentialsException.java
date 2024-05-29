package com.server.survey.auth.exception;

import com.server.survey.exception.UnauthorizedException;

public class BadCredentialsException extends UnauthorizedException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
