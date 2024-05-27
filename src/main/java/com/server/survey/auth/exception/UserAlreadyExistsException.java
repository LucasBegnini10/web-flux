package com.server.survey.auth.exception;

import com.server.survey.exception.BadRequestException;

public class UserAlreadyExistsException extends BadRequestException {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
