package com.kay.Payment.stripe;

import com.kay.Payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private Long orderId;
    private String paymentStatus;
    private String userId;
    private Map<Long,Integer> productDetails;
}
