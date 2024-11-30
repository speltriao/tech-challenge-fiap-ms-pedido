package com.galega.order.adapters.in.queue.sqs.dto;

import com.galega.order.domain.enums.PaymentStatusEnum;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
    @JsonProperty("payedAt")
    private LocalDateTime payedAt;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("gateway")
    private String gateway;

    @JsonProperty("externalId")
    private String externalId;

    @JsonProperty("status")
    private PaymentStatusEnum status;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("orderId")
    private UUID orderId;
}
