package com.server.survey.auth;

import com.server.survey.auth.dto.AuthDTO;
import com.server.survey.auth.dto.AuthResponseDTO;
import com.server.survey.auth.exception.BadCredentialsException;
import com.server.survey.auth.exception.UserAlreadyExistsException;
import com.server.survey.auth.exception.UserNotFoundException;
import com.server.survey.user.User;
import com.server.survey.user.UserService;
import com.server.survey.user.dto.CreateUserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthService(
            UserService userService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<AuthResponseDTO> auth(AuthDTO authDTO) {
        return Mono.just(authDTO)
                .flatMap(this::authenticate)
                .map(jwtService::generateToken)
                .map(AuthResponseDTO::new);
    }

    private Mono<User> authenticate(AuthDTO authDTO) {
        return Mono.just(authDTO)
                .flatMap(dto -> userService.findUserByEmail(dto.getEmail()))
                .flatMap(user -> {
                    if (passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
                        return Mono.just(user);
                    }

                    return Mono.error(new BadCredentialsException("Bad credentials!"));

                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found!")));
    }

    public Mono<User> register(CreateUserDTO createUserDTO) {
        return Mono.just(createUserDTO.getEmail())
                .flatMap(this::checkIfUserExists)
                .switchIfEmpty(createAndSaveUser(createUserDTO));
    }

    private Mono<User> checkIfUserExists(String email) {
        return userService.findUserByEmail(email)
                .flatMap(user -> Mono.<User>error(new UserAlreadyExistsException("User already exists")));
    }

    private Mono<User> createAndSaveUser(CreateUserDTO createUserDTO) {
        return Mono.defer(() -> {
            User user = User.builder()
                    .withEmail(createUserDTO.getEmail())
                    .withName(createUserDTO.getName())
                    .withPassword(this.passwordEncoder.encode(createUserDTO.getPassword()))
                    .withCreatedAt()
                    .build();

            return this.userService.save(user);
        });
    }

}
