package com.ecommerce.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    // For RateLimiting
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1,1,1);
    }

    // For RateLimiting
    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product", r -> r
                        .path("/api/products/**")
                        .filters(f-> f
                                .retry(retryConfig-> retryConfig
                                        .setRetries(10)
                                        .setMethods(HttpMethod.GET))
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
                                .setName("gatewayBreaker")
                                .setFallbackUri("forward:/fallback/orders")))
                        .uri("lb://ORDER"))
                .route("user", r -> r
                        .path("/api/users/**", "/api/user/**")
                        .filters(f->f.circuitBreaker(config -> config
                                .setName("gatewayBreaker")
                                .setFallbackUri("forward:/fallback/users")))
                        .uri("lb://USER"))
                .route("eureka-server",r -> r
                        .path("/eureka/main")
                        .filters(s -> s.rewritePath("/eureka/main","/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static",r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
