package com.server.survey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex){
        return Mono.just(ex)
                .map(ErrorResponse::badRequest)
                .map(error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error))
                .block();

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex){
        return Mono.just(ex)
                .map(ErrorResponse::notFound)
                .map(error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error))
                .block();

    }
}
