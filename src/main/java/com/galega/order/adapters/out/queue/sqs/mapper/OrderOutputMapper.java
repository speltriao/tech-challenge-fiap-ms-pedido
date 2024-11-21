package com.galega.order.adapters.out.queue.sqs.mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.out.queue.sqs.dto.PaymentDTO;

import java.util.UUID;

public class OrderOutputMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String orderIdtoJson(UUID orderId) {
		var paymentDTO = new PaymentDTO(orderId);
		try {
			return objectMapper.writeValueAsString(paymentDTO);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting OrderDTO to JSON", e);
		}
	}
}