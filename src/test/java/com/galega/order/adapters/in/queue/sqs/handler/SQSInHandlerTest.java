package com.galega.order.adapters.in.queue.sqs.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.in.queue.sqs.dto.PaymentDTO;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.enums.PaymentStatusEnum;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;


import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

import static java.time.LocalDateTime.*;
import static org.mockito.Mockito.*;

class SQSInHandlerTest {

	@Mock
	private IOrderUseCase orderUseCase;

	@Mock
	private SQSOutHandler sqsOutHandler;

	@Mock
	private DataSource dataSource;

	@InjectMocks
	private SQSInHandler sqsInHandler;

	private SqsClient mockSqsClient;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		MockitoAnnotations.openMocks(this);

		var mockSqsClient = mock(SqsClient.class);
		this.mockSqsClient = mockSqsClient;

		var objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		this.objectMapper = objectMapper;

		Field field = BaseSQSHandler.class.getDeclaredField("sqsClient");
		field.setAccessible(true);
		field.set(sqsInHandler, mockSqsClient);
	}

	private UUID mockQueue(PaymentStatusEnum paymentStatus) throws JsonProcessingException {
		var uuid = UUID.randomUUID();
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setPayedAt(now());
		paymentDTO.setAmount(BigDecimal.valueOf(100.0));
		paymentDTO.setGateway("PayPal");
		paymentDTO.setExternalId("ext1234");
		paymentDTO.setStatus(paymentStatus);
		paymentDTO.setId(uuid);
		paymentDTO.setOrderId(uuid);

		String messageBody = objectMapper.writeValueAsString(paymentDTO);

		Message message = Message.builder()
				.messageId("1")
				.body(messageBody)
				.receiptHandle("receipt-handle-123")
				.build();

		ReceiveMessageResponse receiveMessageResponse = ReceiveMessageResponse.builder()
				.messages(Collections.singletonList(message))  // Return a list with one message
				.build();

		when(mockSqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
				.thenReturn(receiveMessageResponse);

		return uuid;
	}

	@Test
	public void processPendingPayment() throws Exception {
		// Arrange
		var pending = PaymentStatusEnum.PENDING; // Use PENDING status
		var orderId = UUID.randomUUID(); // Mocked orderId
		var paymentDTO = new PaymentDTO(
				LocalDateTime.now(),          // payedAt
				BigDecimal.valueOf(41.9),     // amount
				"MockGateway",                // gateway
				"ExternalId123",              // externalId
				pending,                      // status (PENDING)
				UUID.randomUUID(),            // id
				orderId                       // orderId
		);

		// Mock the nested JSON structure for the "Message"
		var messageJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(paymentDTO);

		// Escape the messageJson so it can be included properly as a string inside the "Message" field
		var escapedMessageJson = messageJson.replace("\"", "\\\"");

		var snsMessage = String.format(""" 
    {
      "Type" : "Notification",
      "MessageId" : "b662ffb1-6d88-54d0-a019-a074771e16c1",
      "TopicArn" : "arn:aws:sns:us-east-1:775370709632:PaymentUpdated",
      "Message" : "%s",
      "Timestamp" : "2024-11-30T13:51:18.594Z",
      "SignatureVersion" : "1",
      "Signature" : "MockedSignature",
      "SigningCertURL" : "MockedURL",
      "UnsubscribeURL" : "MockedUnsubscribeURL"
    }
    """, escapedMessageJson);

		// Mock the response with the SNS message containing the PENDING payment
		var message = Message.builder().body(snsMessage).build();
		var response = ReceiveMessageResponse.builder().messages(message).build();

		// Mock receiveMessage to return the message
		when(mockSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

		// Act
		when(orderUseCase.processOrderPayment(orderId, pending)).thenReturn(true);
		sqsInHandler.listenToQueue();

		// Assert
		verify(orderUseCase, times(1)).processOrderPayment(orderId, pending);
		verify(mockSqsClient, times(1)).deleteMessage(any(Consumer.class));  // Ensure the message is deleted after processing
	}


	@Test
	public void processApprovedPayment() throws Exception {
		// Arrange
		var approved = PaymentStatusEnum.APPROVED;
		var orderId = UUID.randomUUID(); // Mocked orderId
		var paymentDTO = new PaymentDTO(
				LocalDateTime.now(),          // payedAt
				BigDecimal.valueOf(41.9),     // amount
				"MockGateway",                // gateway
				"ExternalId123",              // externalId
				approved,                     // status
				UUID.randomUUID(),            // id
				orderId                       // orderId
		);

		// Mock the nested JSON structure for the PaymentDTO
		var messageJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(paymentDTO);

		// Escape the messageJson so it can be included properly as a string inside the "Message" field
		var escapedMessageJson = messageJson.replace("\"", "\\\"");

		var snsMessage = String.format("""
        {
          "Type" : "Notification",
          "MessageId" : "b662ffb1-6d88-54d0-a019-a074771e16c1",
          "TopicArn" : "arn:aws:sns:us-east-1:775370709632:PaymentUpdated",
          "Message" : "%s",
          "Timestamp" : "2024-11-30T13:51:18.594Z",
          "SignatureVersion" : "1",
          "Signature" : "MockedSignature",
          "SigningCertURL" : "MockedURL",
          "UnsubscribeURL" : "MockedUnsubscribeURL"
        }
        """, escapedMessageJson);

		// Mock the response
		var message = Message.builder().body(snsMessage).build();
		var response = ReceiveMessageResponse.builder().messages(message).build();

		// Mock receiveMessage
		when(mockSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

		// Act
		when(orderUseCase.processOrderPayment(orderId, approved)).thenReturn(true);
		sqsInHandler.listenToQueue();

		// Assert
		verify(orderUseCase, times(1)).processOrderPayment(orderId, approved);
		verify(mockSqsClient, times(1)).deleteMessage(any(Consumer.class));
	}


}
