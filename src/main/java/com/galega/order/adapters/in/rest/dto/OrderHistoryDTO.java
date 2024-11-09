package com.fiap.techchallenge_order.adapters.in.rest.dto;

import com.fiap.techchallenge_order.domain.entity.OrderHistory;
import com.fiap.techchallenge_order.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDTO {
	private OrderStatus previousStatus;
	private OrderStatus lastStatus;
	private LocalDateTime moment;

	public OrderHistoryDTO(OrderHistory orderHistory){
		this.previousStatus = orderHistory.getPreviousStatus();
		this.lastStatus = orderHistory.getLastStatus();
		this.moment = orderHistory.getMoment();
	}
}
