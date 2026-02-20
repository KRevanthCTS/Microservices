package com.example.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        try {
            byte[] decoded = Decoders.BASE64.decode(jwtSecret);
            if (decoded.length > 0) {
                keyBytes = decoded;
            }
        } catch (Exception ignored) {
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow anonymous access to auth endpoints and public resources
        if (path.startsWith("/auth") || path.startsWith("/public")
                || path.startsWith("/swagger") || path.startsWith("/v3")
                || path.contains("/v3/api-docs") || path.contains("/swagger-ui")) {
            return chain.filter(exchange);
        }

        // For /admin require ADMIN role; for /user require any valid token
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role") != null ? claims.get("role").toString() : null;
            String userId = claims.get("userId") != null ? claims.get("userId").toString() : null;
            String email = claims.getSubject();

            if (path.startsWith("/admin") && (role == null || !role.equalsIgnoreCase("ADMIN"))) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Forward user info to downstream services by mutating the request headers
            org.springframework.http.server.reactive.ServerHttpRequest req = exchange.getRequest().mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-User-Role", role != null ? role : "")
                    .header("X-User-Email", email != null ? email : "")
                    .build();

            ServerWebExchange mutated = exchange.mutate().request(req).build();
            return chain.filter(mutated);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
