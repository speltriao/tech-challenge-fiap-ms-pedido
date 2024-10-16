package com.fiap.techchallenge.adapters.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.techchallenge.domain.entity.Order;
import com.fiap.techchallenge.domain.entity.OrderHistory;
import com.fiap.techchallenge.domain.entity.ProductAndQuantity;
import com.fiap.techchallenge.domain.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
	private UUID id;
	private UUID customerId;
	private Integer orderNumber;
	private BigDecimal amount;
	private OrderStatus status;
	private LocalDateTime createdAt;
	private long waitingTimeInSeconds;
	private List < ProductAndQuantity > products;
	private List < OrderHistory > history;

	public OrderDTO(Order order) {
		this.id = order.getId();
		this.customerId = order.getCustomerId();
		this.orderNumber = order.getOrderNumber();
		this.amount = order.getAmount();
		this.status = order.getStatus();
		this.createdAt = order.getCreatedAt();
		this.waitingTimeInSeconds = order.getWaitingTimeInSeconds();
		this.products = order.getProducts();
		this.history = order.getHistory();
	}
}