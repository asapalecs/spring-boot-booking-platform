package com.airbnb.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/users/login", "/api/users/register", "/actuator");

    private final SecretKey signingKey;

    public AuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest incoming = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove("X-User-Id");
                    h.remove("X-User-Email");
                })
                .build();
        exchange = exchange.mutate().request(incoming).build();

        String path = incoming.getURI().getPath();
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)
                || (incoming.getMethod().matches("GET") && isPublicRead(path))) {
            return chain.filter(exchange);
        }

        String authHeader = incoming.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return reject(exchange);
        }

        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(authHeader.substring(7))
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return reject(exchange);
        }

        ServerHttpRequest request = incoming.mutate()
                .header("X-User-Id", String.valueOf(claims.get("userId")))
                .header("X-User-Email", claims.getSubject())
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    private boolean isPublicRead(String path) {
        return path.startsWith("/api/properties") || path.startsWith("/api/reviews");
    }

    private Mono<Void> reject(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
