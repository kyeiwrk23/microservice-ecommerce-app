package com.kay.Payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private String paymentName;
    private String paymentMethod;
    private String userId;
    private String paymentStatus;
    private Integer orderId;
    private BigDecimal totalAmount;
}
