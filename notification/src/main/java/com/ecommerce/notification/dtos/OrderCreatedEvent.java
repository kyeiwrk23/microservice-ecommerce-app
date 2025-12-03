package com.ecommerce.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private String userId;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
}
