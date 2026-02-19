package com.kay.Payment.controller;

import com.kay.Payment.dtos.*;
import com.kay.Payment.service.PaymentServiceImpl;
import com.kay.Payment.stripe.StripeServiceImpl;
import com.kay.Payment.stripe.WebHookRequest;
import com.kay.Payment.webclientorderConsumer.WebClientOrderConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    private final StripeServiceImpl stripeService;

    private final WebClientOrderConsumer OrderConsumer;

    private final StreamBridge streamBridge;


    @PostMapping("/{userId}/order/{orderId}")
    public Mono<ResponseEntity<StripeResponseDto>> getStripePayments(
            @PathVariable String userId,
            @PathVariable Long orderId) {

        log.info("getStripePayments: userId = {}, orderId = {}", userId, orderId);

        return Mono.fromCallable(() -> OrderConsumer.getOrderPaymentDto(userId, orderId))
                .subscribeOn(Schedulers.boundedElastic()) // run blocking code off the event loop
                .doOnNext(dto -> log.info("getStripePayments email: dto = {}", dto.getEmail()))
                .flatMap(dto ->
                        Mono.fromCallable(() -> stripeService.checkOutSession(dto))
                                .subscribeOn(Schedulers.boundedElastic())
                                .doOnNext(checkOut ->
                                        log.info("getStripePayments: checkOut = {}", checkOut.getSessionUrl())
                                )
                )
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> {
                    log.error("Error in getStripePayments", ex);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }


    @PostMapping("/webhook")
    public Mono<ResponseEntity<Void>> handleWebhook(@RequestHeader Map<String,String> headers,
                                                    @RequestBody Mono<String> payload) {
        return  payload.map(pay ->{
        log.info("handleWebhook: headers = {}, payload = {}", headers.get("Stripe-Signature"), pay);

         stripeService.parseWebhookRequest(new WebHookRequest(headers,pay)).ifPresent(
                 webhook -> {
                     log.info("handleWebhook: userId = {}", webhook.getUserId());
                     log.info("handleWebhook: orderId = {}", webhook.getOrderId());
                     log.info("handleWebhook: payload = {}", webhook.getPaymentStatus());

                     for(Map.Entry<Long,Integer> entry : webhook.getProductDetails().entrySet()){
                         log.info("handleWebhook: productDetails = {}", entry.getValue());
                         log.info("handleWebhook: productName = {}", entry.getKey());
                     }
                     streamBridge.send("handleWebhook-out-0", webhook);

                 });
            return ResponseEntity.ok().build();
        });


    }

    @PostMapping("/test/{userId}")
    public ResponseEntity<String> test(@PathVariable String userId)
    {
        log.info("test: userId = {}", userId);
        return ResponseEntity.ok("success");
    }


}
