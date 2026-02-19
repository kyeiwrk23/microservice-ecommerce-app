package com.ecommerce.gateway;

import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class GatewayConfig {

    // For RateLimiting
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(20,40,1);
    }

    // For RateLimiting
    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress());
    }
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("payment-webhook", r -> r
                        .path("/api/payments/webhook","/api/payments/webhook/**")
                        .filters(f -> f
                                .preserveHostHeader()
                                .removeRequestHeader("Expect")

                        )
                        .uri("lb://PAYMENT"))
                .route("product", r -> r
                        .path("/api/products/**")
                        .filters(f-> f
                                .requestRateLimiter(config->config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver()))
                                .circuitBreaker(config -> config
                                .setName("gatewayBreaker")
                                .setFallbackUri("forward:/fallback/products")))
                        .uri("lb://PRODUCT"))
                .route("order", r -> r
                        .path("/api/cart/**", "/api/orders/**")
                        .filters(f->f.circuitBreaker(config -> config
                                .setName("orderBreaker")
                                .setFallbackUri("forward:/fallback/orders")))
                        .uri("lb://ORDER"))
                .route("user", r -> r
                        .path("/api/users/**","/api/users")
                        .filters(f->f.circuitBreaker(config -> config
                                .setName("userBreaker")
                                .setFallbackUri("forward:/fallback/users")))
                        .uri("lb://USER"))
                .route("payment", r -> r
                        .path("/api/payments/**")
                        .filters(f ->f
                                .circuitBreaker(config -> config
                                .setName("paymentBreaker")
                                .setFallbackUri("forward:/fallback/payments")))
                        .uri("lb://PAYMENT"))
                .route("eureka-server",r -> r
                        .path("/eureka/main")
                        .filters(s -> s.rewritePath("/eureka/main","/"))
                        .uri("http://eureka:8761"))
                .route("eureka-static-server",r -> r
                        .path("/eureka/**")
                        .uri("http://eureka:8761"))
                .build();
    }


}
