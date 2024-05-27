package com.server.survey.auth;

import com.server.survey.auth.exception.UserAlreadyExistsException;
import com.server.survey.user.User;
import com.server.survey.user.UserService;
import com.server.survey.user.dto.CreateUserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<User> register(CreateUserDTO createUserDTO) {
        String email = createUserDTO.getEmail();
        return Mono.just(email)
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
