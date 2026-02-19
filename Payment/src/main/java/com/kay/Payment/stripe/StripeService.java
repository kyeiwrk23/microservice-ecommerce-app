package com.kay.Payment.stripe;

import com.kay.Payment.dtos.OrderPaymentDto;
import com.kay.Payment.dtos.StripeProductRequestDto;
import com.kay.Payment.dtos.StripeResponseDto;
import com.stripe.exception.StripeException;

import java.util.Optional;

public interface StripeService {
    StripeResponseDto checkOutSession(OrderPaymentDto orderDto) throws StripeException;

    Optional<PaymentResult> parseWebhookRequest(WebHookRequest request);
}
