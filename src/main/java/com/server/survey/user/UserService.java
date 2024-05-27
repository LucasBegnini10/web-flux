package com.server.survey.user;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Flux<User> getAll(){
        return this.userRepository.findAll();
    }

    public Mono<User> save(User user){
        return this.userRepository.save(user);
    }

}
