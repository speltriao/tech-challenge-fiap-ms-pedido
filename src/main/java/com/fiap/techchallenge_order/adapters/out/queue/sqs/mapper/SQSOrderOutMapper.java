package com.fiap.techchallenge_order.adapters.out.queue.sqs.mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge_order.adapters.in.rest.dto.OrderDTO;

public class SQSOrderOutMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String orderDTOtoJson(OrderDTO orderDTO) {
		try {
			return objectMapper.writeValueAsString(orderDTO);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting OrderDTO to JSON", e);
		}
	}
}