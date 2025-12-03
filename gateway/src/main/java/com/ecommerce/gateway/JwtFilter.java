package com.ecommerce.gateway;


import org.springframework.http.HttpStatus;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//@Component
public class JwtFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
      if(authHeader == null || !authHeader.startsWith("Bearer ")){
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          return exchange.getResponse().setComplete();
      }
      // TODO: validate JWT Token

        return chain.filter(exchange);
    }
}
