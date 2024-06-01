package com.server.survey.auth.service;

import com.server.survey.auth.AuthService;
import com.server.survey.auth.JwtService;
import com.server.survey.auth.dto.AuthDTO;
import com.server.survey.auth.dto.AuthResponseDTO;
import com.server.survey.auth.exception.BadCredentialsException;
import com.server.survey.auth.exception.UserAlreadyExistsException;
import com.server.survey.auth.exception.UserNotFoundException;
import com.server.survey.user.User;
import com.server.survey.user.UserService;
import com.server.survey.user.dto.CreateUserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void should_returnAuthResponseDTO_when_UserAuthenticateWithCorrectCredentials() {
        String email = "email";
        String password = "password";
        String token = "token";

        AuthDTO authDTO = new AuthDTO(email, password);
        User user = User.builder().withEmail(email).withPassword(password).build();

        when(userService.findUserByEmail(authDTO.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(authDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        Mono<AuthResponseDTO> authResponseDTOMono = authService.auth(authDTO);

        Assertions.assertNotNull(authResponseDTOMono);

        Assertions.assertEquals(authResponseDTOMono.block().getToken(), token);
    }

    @Test()
    public void should_returnUserNotFound_when_UserAuthenticateWithIncorrectEmail() {
        String email = "email";
        String password = "password";

        AuthDTO authDTO = new AuthDTO(email, password);

        when(userService.findUserByEmail(authDTO.getEmail())).thenReturn(Mono.empty());

        UserNotFoundException ex =
                Assertions.assertThrows(UserNotFoundException.class, () -> authService.auth(authDTO).block());

        Assertions.assertTrue(Objects.nonNull(ex));
        Assertions.assertEquals(ex.getMessage(), "User not found!");
    }

    @Test
    public void should_returnBadCredentials_when_UserAuthenticateWithBadCredentials() {
        String email = "email";
        String password = "password";

        AuthDTO authDTO = new AuthDTO(email, password);
        User user = User.builder().withEmail(email).withPassword(password).build();

        when(userService.findUserByEmail(authDTO.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(authDTO.getPassword(), user.getPassword())).thenReturn(false);

        BadCredentialsException ex =
                Assertions.assertThrows(BadCredentialsException.class, () -> authService.auth(authDTO).block());

        Assertions.assertTrue(Objects.nonNull(ex));
        Assertions.assertEquals(ex.getMessage(), "Bad credentials!");
    }

    @Test
    public void should_returnMonoUser_when_userRegisterWithCorrectDTO() {
        CreateUserDTO createUserDTO = new CreateUserDTO("Lucas", "email", "password");
        String encodedPassword = "encodedPassword";

        User user = User.builder()
                .withEmail(createUserDTO.getEmail())
                .withName(createUserDTO.getName())
                .withPassword(encodedPassword)
                .build();


        when(userService.findUserByEmail(createUserDTO.getEmail())).thenReturn(Mono.empty());
        when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn(encodedPassword);
        when(userService.save(Mockito.any(User.class))).thenReturn(Mono.just(user));

        Mono<User> userRegistered = authService.register(createUserDTO);

        StepVerifier.create(userRegistered)
                .expectNext(user)
                .verifyComplete();

        Assertions.assertEquals(userRegistered.block(), user);
        Assertions.assertEquals(userRegistered.block().getEmail(), createUserDTO.getEmail());
        Assertions.assertEquals(userRegistered.block().getName(), createUserDTO.getName());
        Assertions.assertEquals(userRegistered.block().getPassword(), encodedPassword);
    }

    @Test
    public void should_returnUserAlreadyExistsException_when_userRegisterWithAlreadyExistingEmail() {
        CreateUserDTO createUserDTO = new CreateUserDTO("Lucas", "email", "password");

        when(userService.findUserByEmail(createUserDTO.getEmail())).thenReturn(Mono.just(User.builder().build()));

        UserAlreadyExistsException ex =
                Assertions.assertThrows(UserAlreadyExistsException.class, () -> authService.register(createUserDTO).block());

        Assertions.assertTrue(Objects.nonNull(ex));
        Assertions.assertEquals(ex.getMessage(), "User already exists");
    }
}
