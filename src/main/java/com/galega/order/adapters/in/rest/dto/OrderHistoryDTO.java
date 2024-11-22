package com.galega.order.adapters.in.rest.dto;


import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.enums.OrderStatusEnum;
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
	private OrderStatusEnum previousStatus;
	private OrderStatusEnum lastStatus;
	private LocalDateTime moment;

	public OrderHistoryDTO(OrderHistory orderHistory){
		this.previousStatus = orderHistory.getPreviousStatus();
		this.lastStatus = orderHistory.getLastStatus();
		this.moment = orderHistory.getMoment();
	}
}
