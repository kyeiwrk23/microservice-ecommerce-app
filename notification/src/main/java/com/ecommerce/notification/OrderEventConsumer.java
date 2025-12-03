package com.ecommerce.notification;

import com.ecommerce.notification.dtos.OrderCreatedEvent;
import com.ecommerce.notification.dtos.OrderItemDTO;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;


@Service
@Slf4j
public class OrderEventConsumer {

/*
    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    public void handleOrderEvent(OrderCreatedEvent orderEvent) {

        long orderId = orderEvent.getOrderId();
        String userId = orderEvent.getUserId();
        LocalDate orderDate = orderEvent.getOrderDate();
        String status = orderEvent.getStatus();
        BigDecimal totalAmount = orderEvent.getTotalAmount();
        List<OrderItemDTO> orderItems = orderEvent.getOrderItems();

        printOrder(orderId,userId,orderDate,status, totalAmount,orderItems);


    }*/

    @Bean
    public Consumer<OrderCreatedEvent> orderCreatedEventConsumer() {
        return event -> {
            printOrder(event.getOrderId(),event.getUserId(),event.getOrderDate(),event.getStatus(),event.getTotalAmount(),event.getOrderItems());
        };
    }

    public void printOrder(long orderId, String userID, LocalDate orderDate, String status, BigDecimal totalAmount,List<OrderItemDTO> item){
        System.out.println("    Order ID: " + orderId );
        System.out.println("     User ID: " + userID );
        System.out.println("Date ordered: " + orderDate );
        System.out.println("      Status: " + status );
        System.out.println("Total amount: " + totalAmount );

        for(OrderItemDTO item1 : item) {
            System.out.println("  Product ID: " + item1.getProductId());
            System.out.println("Product Name: " + item1.getProductName());
            System.out.println("    Quantity: " + item1.getQuantity());
        }
    }




//    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
//    public void handleOrderEvent(Map<String, Object> orderEvent) {
//        System.out.println("Received order event: " + orderEvent);
//
//        long orderId = Long.parseLong(orderEvent.get("orderId").toString());
//        String status = orderEvent.get("status").toString();
//        System.out.println("order ID: " + orderId + " status: " + status);
//    }

}
