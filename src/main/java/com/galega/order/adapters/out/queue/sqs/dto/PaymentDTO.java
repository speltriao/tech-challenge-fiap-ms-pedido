package com.galega.order.adapters.out.queue.sqs.dto;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class PaymentDTO {
	private UUID orderId;
}
