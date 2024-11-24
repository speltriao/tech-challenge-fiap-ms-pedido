package com.galega.order.adapters.out.queue.sqs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.galega.order.adapters.AppConfig;
import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.out.queue.sqs.mapper.OrderOutputMapper;
import com.galega.order.domain.enums.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SQSOutHandlerTest {

	@InjectMocks
	private SQSOutHandler sqsOutHandler;  // Class under test


	private SqsClient mockSqsClient;
	private ObjectMapper objectMapper;
	private OrderDTO order;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		var mockSqsClient = mock(SqsClient.class);
		this.mockSqsClient = mockSqsClient;

		order = OrderDTO.builder()
				.id(UUID.randomUUID())
				.customerId(UUID.randomUUID())
				.orderNumber(12345)
				.amount(new BigDecimal("99.99"))
				.status(OrderStatusEnum.CREATED)  // Assuming this maps to the OrderStatusEnum
				.createdAt(LocalDateTime.now())
				.waitingTimeInSeconds(120)
				.build();

		Field field = BaseSQSHandler.class.getDeclaredField("sqsClient");
		field.setAccessible(true);
		field.set(sqsOutHandler, mockSqsClient);
	}

	@Test
	void testSendOrderMessage_success() {
		String messageBody = "{\"order\":\"details\"}";
		SendMessageResponse sendMessageResponse = SendMessageResponse.builder().messageId("12345").build();

		when(mockSqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(sendMessageResponse);

		sqsOutHandler.sendOrderMessage(order);

		verify(mockSqsClient, atMostOnce()).sendMessage(any(SendMessageRequest.class));
	}

	@Test
	void testSendOrderMessage_failure() {
		sqsOutHandler.sendOrderMessage(null);  // Call the method under test

		verify(mockSqsClient, never()).sendMessage(any(SendMessageRequest.class));  // SQS should not be called if JSON conversion fails
	}
}
