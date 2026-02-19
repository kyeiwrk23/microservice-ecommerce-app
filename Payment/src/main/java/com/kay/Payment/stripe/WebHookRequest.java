package com.kay.Payment.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebHookRequest {
    private Map<String, String> headers;
    private String payload;
}
