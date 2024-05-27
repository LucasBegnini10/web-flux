package com.server.survey.auth;

import com.server.survey.user.User;
import com.server.survey.user.dto.CreateUserDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping()
    public String auth(){
        return "Auth";
    }

    @PostMapping("register")
    public Mono<User> register(@RequestBody CreateUserDTO createUserDTO){
        return this.authService.register(createUserDTO);
    }
}
