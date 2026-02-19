package com.kay.Payment.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripeProductRequestDto {

    private Long amount;
    private Long quantity;
    private String name;
    private String currency;

}
