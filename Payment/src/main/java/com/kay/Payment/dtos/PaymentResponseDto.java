package com.kay.Payment.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private String paymentStatus;
    private Integer orderId;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
}
