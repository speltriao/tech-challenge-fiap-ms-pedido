package com.galega.order.adapters.in.queue.sqs.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.galega.order.adapters.in.queue.sqs.dto.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class SQSOrderInMapper {
	protected static final Logger logger = LoggerFactory.getLogger(SQSOrderInMapper.class);

	public static PaymentDTO mapUpdateOrderStatusDTO(String messageBody) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		try {
			return objectMapper.readValue(messageBody, PaymentDTO.class);
		} catch (IOException e) {
			logger.error("Failed to parse message body: {}", messageBody, e);
			throw new RuntimeException("Invalid message format", e);
		}
	}
}
