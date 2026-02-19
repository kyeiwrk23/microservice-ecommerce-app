package com.kay.Payment.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EventListener;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @NotBlank(message = "Payment status can't be blank")
    private String paymentStatus;

    @NotBlank(message = "Stripe PaymentId can't be blank")
    private String stripePaymentId;

    @NotNull
    private Long orderId;

    @NotBlank(message = "UserId can't be blank")
    private String userId;

    @NotNull(message = "Can't be null")
    private BigDecimal totalAmount;

    @CreatedDate
    private LocalDateTime paymentDate;
}
