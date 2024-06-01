package com.server.survey.auth.controller;

import com.server.survey.auth.AuthController;
import com.server.survey.auth.AuthService;
import com.server.survey.auth.dto.AuthDTO;
import com.server.survey.auth.dto.AuthResponseDTO;
import com.server.survey.auth.exception.BadCredentialsException;
import com.server.survey.auth.exception.UserAlreadyExistsException;
import com.server.survey.auth.exception.UserNotFoundException;
import com.server.survey.exception.ErrorHandler;
import com.server.survey.exception.ErrorResponse;
import com.server.survey.user.User;
import com.server.survey.user.dto.CreateUserDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Test
    public void should_returnAuthResponseDTO_when_auth() {
        WebTestClient client = WebTestClient.bindToController(authController).build();

        when(this.authService.auth(mock(AuthDTO.class))).thenReturn(Mono.just(new AuthResponseDTO()));

        client.post()
                .uri("/api/v1/auth")
                .bodyValue(new AuthDTO())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponseDTO.class);
    }

    @Test
    public void should_returnUnauthorized_when_authWithInvalidCredentials(){
        WebTestClient client = WebTestClient.bindToController(authController).controllerAdvice(new ErrorHandler()).build();

        when(this.authService.auth(any())).thenReturn(Mono.error(new BadCredentialsException("Bad credentials!")));

        client.post()
                .uri("/api/v1/auth")
                .bodyValue(new AuthDTO())
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .consumeWith(response -> {
                    ErrorResponse error = response.getResponseBody();
                    assert error != null;
                    assert error.getMessage().equals("Bad credentials!");
                });
    }

    @Test
    public void should_returnUserNotFound_when_authWithNonExistingUser(){
        WebTestClient client = WebTestClient.bindToController(authController).controllerAdvice(new ErrorHandler()).build();

        String message = "User not found!";

        when(this.authService.auth(any())).thenReturn(Mono.error(new UserNotFoundException(message)));

        client.post()
                .uri("/api/v1/auth")
                .bodyValue(new AuthDTO())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .consumeWith(response -> {
                    ErrorResponse error = response.getResponseBody();
                    assert error != null;
                    assert error.getMessage().equals(message);
                });
    }

    @Test
    public void should_returnUser_when_register() {
        WebTestClient client = WebTestClient.bindToController(authController).build();

        when(this.authService.register(mock(CreateUserDTO.class))).thenReturn(Mono.just(new User()));

        client.post()
                .uri("/api/v1/auth/register")
                .bodyValue(new CreateUserDTO())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class);
    }

    @Test
    public void should_returnBadRequest_when_registerUserThatAlreadyExists() {
        WebTestClient client =
                WebTestClient
                        .bindToController(authController).controllerAdvice(new ErrorHandler()).build();

        String message = "User already exists";

        when(this.authService.register(any()))
                .thenReturn(Mono.<User>error(new UserAlreadyExistsException(message)));

        client.post()
                .uri("/api/v1/auth/register")
                .bodyValue(new CreateUserDTO())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .consumeWith(response -> {
                    ErrorResponse error = response.getResponseBody();
                    assert error != null;
                    assert error.getMessage().equals(message);
                });
    }
}
