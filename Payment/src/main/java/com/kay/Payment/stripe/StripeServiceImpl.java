package com.kay.Payment.stripe;


import com.kay.Payment.dtos.OrderPaymentDto;
import com.kay.Payment.dtos.OrderPaymentItemsDto;
import com.kay.Payment.dtos.StripeResponseDto;
import com.kay.Payment.model.Payment;
import com.kay.Payment.model.PaymentStatus;
import com.kay.Payment.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    //stripe - API
    //-> productName, amount,quantity, currency
    //-> return sessionId and url

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${stripe.secretkey}")
    private String secretKey;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Value("${clientBaseUrl}")
    private String clientBaseUrl;

    private final PaymentRepository paymentRepository;



    @Override
    public StripeResponseDto checkOutSession(OrderPaymentDto orderDto) {
        Stripe.apiKey = secretKey;
        String name = orderDto.getFistName()+ " " + orderDto.getLastName();
        Customer customer = CustomerInfo.findOrCreateNewCustomer(orderDto.getEmail(), name);
        String orderId = String.valueOf(orderDto.getOrderId());
        String userId = String.valueOf(orderDto.getUserId());
        log.info("order_id: {}", orderId);

        SessionCreateParams.Builder sessionBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
                                        .putMetadata("order_id",orderId)
                                        .setDescription(userId)
                                        .build())
                        .putMetadata("order_id",orderId)
                        .putMetadata("user_id",userId)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(clientBaseUrl + "/api/payment/success")
                        .setCancelUrl(clientBaseUrl + "/api/payment/failure");

        ObjectMapper mapper = new ObjectMapper();
        String productMetadata;


        productMetadata = mapper.writeValueAsString(
                orderDto.getOrderPaymentItems()
                .stream()
                .map(item -> Map.of(
                        "product_Id",item.getProductId(),
                        "productName",item.getProductName(),
                        "productQuantity",item.getQuantity()
                ))
                        .toList()
        );

        sessionBuilder.putMetadata("product_metadata", productMetadata);



        for(OrderPaymentItemsDto items : orderDto.getOrderPaymentItems()) {
            sessionBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(Long.parseLong(items.getQuantity().toString()))
                            .setPriceData(
                                    getPriceData(items))
                            .build());

        }


        Session session = null;
        try {
            session = Session.create(sessionBuilder.build());

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            throw new RuntimeException("Stripe session creation failed");

        }
        return StripeResponseDto
                .builder()
                .status("SUCCESS")
                .message("PAYMENT CREATED")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }



    private static SessionCreateParams.LineItem.PriceData getPriceData(OrderPaymentItemsDto items) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setProductData(
                        getProductData(items)
                )
                .setCurrency("eur")
                .setUnitAmountDecimal(items.getOrderedProductPrice().multiply(BigDecimal.valueOf(100)))
                .build();
    }

    private static SessionCreateParams.LineItem.PriceData.ProductData getProductData(OrderPaymentItemsDto items) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(items.getProductName())
                .putMetadata("product_id", String.valueOf(items.getProductId()))
                .build();
    }



    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebHookRequest request) {

        try {
            PaymentResult result = new PaymentResult();
            var payload = request.getPayload();
            var signature = request.getHeaders().get("Stripe-Signature");

            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);
            System.out.println("Event type: " + event.getType());

            var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                    ()-> new RuntimeException("Could not deserialize stripe event. Check SDK and API version"));

            // Receive webhook data and extract paid products
            if (event.getType().equals("checkout.session.completed")) {
                Session session = (Session) stripeObject;
                String dataJson = session.getMetadata().get("product_metadata");

                ObjectMapper mapper = new ObjectMapper();

                List<Map<String,Object>> products =
                        mapper.readValue(dataJson, new TypeReference<List<Map<String, Object>>>() {});

                Map<Long, Integer> productDetails = new HashMap<>();

                for(Map<String,Object> map : products) {
                    Long productId = Long.parseLong(map.get("product_Id").toString());
                    Integer productQuantity = Integer.parseInt(map.get("productQuantity").toString());
                    productDetails.put(productId, productQuantity);
                }

                String userId = session.getMetadata().get("user_id");
                Long orderId = Long.parseLong(session.getMetadata().get("order_id"));

                result.setProductDetails(productDetails);
                result.setPaymentStatus(PaymentStatus.PAID.name());
                result.setOrderId(orderId);
                result.setUserId(userId);


                // Retrieve payment details from Stripe after successful payment
                BigDecimal amount = BigDecimal.valueOf(session.getAmountSubtotal()/100.0);
                String stripePaymentId = session.getPaymentIntent();

                Payment payment = new Payment();
                payment.setPaymentStatus(PaymentStatus.PAID.name());
                payment.setStripePaymentId(stripePaymentId);
                payment.setUserId(userId);
                payment.setOrderId(orderId);
                payment.setTotalAmount(amount);

                paymentRepository.save(payment);

                return Optional.of(result);

            }


        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid Signature");
        }

        return Optional.ofNullable(null);
    }




}




