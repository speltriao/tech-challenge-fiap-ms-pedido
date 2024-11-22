package com.galega.order.adapters.in.queue.sqs.dto;

import com.galega.order.domain.enums.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class PaymentDTO {
    private LocalDateTime payedAt;
    private BigDecimal amount;
    private String gateway;
    private String externalId;
    private PaymentStatus status;
    private UUID id;
    private UUID orderId;
}