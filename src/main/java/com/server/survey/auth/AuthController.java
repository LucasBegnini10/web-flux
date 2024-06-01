package com.server.survey.auth;

import com.server.survey.auth.dto.AuthDTO;
import com.server.survey.auth.dto.AuthResponseDTO;
import com.server.survey.user.User;
import com.server.survey.user.dto.CreateUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping()
    public Mono<AuthResponseDTO> auth(@RequestBody AuthDTO authDTO){
        return this.authService.auth(authDTO);
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> register(@RequestBody CreateUserDTO createUserDTO){
        return this.authService.register(createUserDTO);
    }
}
