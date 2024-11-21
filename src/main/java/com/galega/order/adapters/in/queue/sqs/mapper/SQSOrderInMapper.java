package com.galega.order.adapters.in.queue.sqs.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galega.order.adapters.in.queue.sqs.dto.UpdateOrderStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class SQSOrderInMapper {
	protected static final Logger logger = LoggerFactory.getLogger(SQSOrderInMapper.class);

	public static UpdateOrderStatusDTO mapUpdateOrderStatusDTO(String messageBody) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(messageBody, UpdateOrderStatusDTO.class);
		} catch (IOException e) {
			logger.error("Failed to parse message body: {}", messageBody, e);
			throw new RuntimeException("Invalid message format", e);
		}
	}
}
