package com.ecommerce.gateway;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CustomConverter customConverter;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        System.out.println("SecurityConfig securityWebFilterChain");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/payments/webhook/**","/api/payments/webhook/**").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/users").permitAll()
                        .pathMatchers(HttpMethod.GET,"/api/users").hasAnyRole("USER","PRODUCT","ORDER")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth->
                        oauth.jwt(jwt-> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(customConverter))))
                .build();

    }

    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter(CustomConverter customConverter) {
        JwtGrantedAuthoritiesConverter scopeConvert = new JwtGrantedAuthoritiesConverter();

        return jwt->{
            Collection<GrantedAuthority> authority = new ArrayList<>();
            authority.addAll(scopeConvert.convert(jwt));
            authority.addAll(customConverter.convert(jwt));

            String principalName = jwt.getClaimAsString("preferred_username");

            return Mono.just(
                    new JwtAuthenticationToken(jwt,authority, principalName)
            );
        };
    }
















}
