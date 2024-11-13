package com.galega.order.adapters.out.notification.sns.mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galega.order.adapters.in.rest.dto.OrderDTO;

public class SNSOrderOutMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String orderDTOtoJson(OrderDTO orderDTO) {
		try {
			return objectMapper.writeValueAsString(orderDTO);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting OrderDTO to JSON", e);
		}
	}
}