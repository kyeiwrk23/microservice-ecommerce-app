package com.ecommerce.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter{
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    /** What this method is doing is that it is intercepting all request
     *  running through the gateway and logging the message to console.
     *  Also use to check all incoming traffic
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Incoming request to: {}", exchange.getRequest().getURI());

        return chain.filter(exchange);

    }





}
