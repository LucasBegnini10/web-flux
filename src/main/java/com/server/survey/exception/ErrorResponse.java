package com.server.survey.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ErrorResponse {

    public ErrorResponse() {
    }

    public ErrorResponse(Builder builder) {
        this.id = builder.id;
        this.status = builder.status;
        this.message = builder.message;
        this.dateTime = builder.dateTime;
    }

    private String id;
    private int status;
    private String message;
    private LocalDateTime dateTime;


    public static ErrorResponse badRequest(BadRequestException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    public static ErrorResponse notFound(NotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    public static ErrorResponse unauthorized(UnauthorizedException ex) {
        return buildError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    public static ErrorResponse buildError(String message, int status) {
        return builder()
                .withId(UUID.randomUUID().toString())
                .withMessage(message)
                .withStatus(status)
                .withDateTime(LocalDateTime.now())
                .build();
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private int status;
        private String message;
        private LocalDateTime dateTime;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}
