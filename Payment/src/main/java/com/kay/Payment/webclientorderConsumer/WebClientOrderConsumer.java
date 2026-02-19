package com.kay.Payment.webclientorderConsumer;

import com.kay.Payment.dtos.OrderPaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;
@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientOrderConsumer {

    public final WebClient webClient;

    public OrderPaymentDto getOrderPaymentDto(String userId, Long orderId) throws ExecutionException, InterruptedException {
        try {
            return webClient.get()
                    .uri("api/orders/{userId}/order/{orderId}",userId,orderId)
                    .retrieve()
                    .bodyToMono(OrderPaymentDto.class)
                    .toFuture()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("Order Service Interrupted!!!!");
        }

        return null;
    }
}
