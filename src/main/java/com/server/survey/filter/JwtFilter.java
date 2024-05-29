package com.server.survey.filter;

import com.server.survey.auth.JwtService;
import com.server.survey.user.RoleType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = recoverToken(exchange);

        if (token == null) return chain.filter(exchange);

        return Mono.just(token)
                .flatMap(jwtService::validateToken)
                .doOnNext(user -> exchange.getAttributes().put("user", user))
                .flatMap(user -> onAuthenticationSuccess(buildAuthentication(user, user.getRole()), new WebFilterExchange(exchange, chain)))
                .then();
    }

    private Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange exchange) {
        ServerWebExchange serverWebExchange = exchange.getExchange();
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        return exchange.getChain().filter(serverWebExchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    private String recoverToken(ServerWebExchange exchange) {
        try {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            assert authHeader != null;

            return authHeader.substring(7);
        } catch (Exception e) {
            return null;
        }
    }

    private Authentication buildAuthentication(
            Object principal,
            RoleType role
    ) {
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                AuthorityUtils.createAuthorityList(role.name())
        );
    }
}
