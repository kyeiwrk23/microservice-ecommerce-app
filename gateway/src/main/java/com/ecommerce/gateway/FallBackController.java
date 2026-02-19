package com.ecommerce.gateway;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class FallBackController {
    @RequestMapping(value = "/fallback/products",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<List<String>> productsFallback(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Product service is Unavailable, Please try again later."));
    }

    @RequestMapping(value = "/fallback/orders", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<String> ordersFallback(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Order service is Unavailable, Please try again later.");
    }

    @RequestMapping(value = "/fallback/users", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<String> usersFallback(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Users service is Unavailable, Please try again later.");
    }

    @RequestMapping(value = "/fallback/payments", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<String> paymentsFallback(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment service is Unavailable, Please try again later.");
    }
}
