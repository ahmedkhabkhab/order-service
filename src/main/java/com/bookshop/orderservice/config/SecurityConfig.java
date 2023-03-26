package com.bookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange ->
                        exchange.pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()) // All requests require authentication.
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt) // Enable OAuth2 resource server support using JWT authentication.
                .requestCache(requestCacheSpec -> requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance())) // Stateless. each request include an access token. No need to keep a session token cache alive between requests.
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Stateless + No browser-based client. No need for CSRF
                .build();
    }

}
