package com.server.survey.auth;

import com.server.survey.auth.exception.BadCredentialsException;
import com.server.survey.user.RoleType;
import com.server.survey.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final String secretKey;
    private final long jwtExpiration;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String key,
            @Value("${application.security.jwt.expiration}") long expiration){
        this.secretKey = key;
        this.jwtExpiration = expiration;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("createdAt", user.getCreatedAt().toString());

        return buildToken(claims);
    }

    private String buildToken(Map<String, Object> claims) {
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(currentTimeMillis + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Mono<User> validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            String roleString = claims.get("role", String.class);
            RoleType role = RoleType.valueOf(roleString);

            String createdAtString = claims.get("createdAt", String.class);
            LocalDateTime createdAt = LocalDateTime.parse(createdAtString);

            User user = User.builder()
                    .withId(claims.get("id", String.class))
                    .withName(claims.get("name", String.class))
                    .withEmail(claims.get("email", String.class))
                    .withRole(role)
                    .withCreatedAt(createdAt)
                    .build();

            return Mono.just(user);
        } catch (Exception e) {
            return Mono.error(new BadCredentialsException("Invalid token"));
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
