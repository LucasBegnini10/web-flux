package com.server.survey.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public Flux<User> getAll(){
        return this.userService.getAll();
    }

    @GetMapping("/me")
    public Mono<User> getMe(ServerWebExchange serverWebExchange){
        return Mono.just(Objects.requireNonNull(serverWebExchange.getAttribute("user")));
    }
}
