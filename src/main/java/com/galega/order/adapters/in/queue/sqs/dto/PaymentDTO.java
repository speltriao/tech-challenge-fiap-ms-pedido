package com.galega.order.adapters.in.queue.sqs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.galega.order.domain.enums.PaymentStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
    private LocalDateTime payedAt;
    private BigDecimal amount;
    private String gateway;
    private String externalId;
    private PaymentStatusEnum status;
    private UUID id;
    private UUID orderId;
}