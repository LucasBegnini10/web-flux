package com.server.survey.auth.service;

import com.server.survey.auth.JwtService;
import com.server.survey.auth.exception.BadCredentialsException;
import com.server.survey.auth.exception.UserNotFoundException;
import com.server.survey.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
public class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "3fde6459f95f4ba4930bf4ddd3d8414b087fe289bb704c5da8ef662f4b772880",
            3600000
    );

    @Test
    public void should_returnToken_when_sendUser() {
        User user = User.builder()
                .withId("1")
                .withName("test")
                .withEmail("test@test.com")
                .withCreatedAt()
                .build();

        String token = jwtService.generateToken(user);

        Assertions.assertNotNull(token);
        Assertions.assertInstanceOf(String.class, token);
    }

    @Test
    public void should_returnMonoUser_when_sendToken(){
        User user = User.builder()
                .withId("1")
                .withName("test")
                .withEmail("test@test.com")
                .withCreatedAt()
                .build();

        String token = jwtService.generateToken(user);

        jwtService.validateToken(token)
                .flatMap(u -> {
                    Assertions.assertEquals(user.getId(), u.getId());
                    Assertions.assertEquals(user.getName(), u.getName());
                    Assertions.assertEquals(user.getEmail(), u.getEmail());
                    return Mono.just(u);
                });
    }

    @Test
    public void should_throwBadCredentials_when_sendInvalidToken(){
        BadCredentialsException ex =
                Assertions.assertThrows(BadCredentialsException.class, () -> jwtService.validateToken("invalidToken").block());

        Assertions.assertEquals("Invalid token", ex.getMessage());
    }
}
