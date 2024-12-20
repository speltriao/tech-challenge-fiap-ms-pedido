package com.galega.order.adapters.out.queue.sqs.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.galega.order.adapters.in.rest.dto.OrderDTO;

public class OrderOutputMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	public static String orderDTOtoJson(OrderDTO order) {
		try {
			return objectMapper.writeValueAsString(order);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting OrderDTO to JSON", e);
		}
	}
}