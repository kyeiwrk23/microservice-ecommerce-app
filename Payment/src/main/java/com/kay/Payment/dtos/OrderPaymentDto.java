package com.kay.Payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentDto {
    private Long orderId;
    private String fistName;
    private String lastName;
    private String email;
    private String userId;
    private List<OrderPaymentItemsDto> orderPaymentItems;
}
