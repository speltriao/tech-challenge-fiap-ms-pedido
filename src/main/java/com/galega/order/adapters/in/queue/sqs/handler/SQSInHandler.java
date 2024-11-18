package com.galega.order.adapters.in.queue.sqs.handler;

import com.galega.order.adapters.AppConfig;
import com.galega.order.adapters.in.queue.sqs.dto.UpdateOrderStatusDTO;
import com.galega.order.adapters.in.queue.sqs.enums.OperationTypes;
import com.galega.order.adapters.in.queue.sqs.mapper.SQSOrderInMapper;
import com.galega.order.adapters.in.rest.dto.CreateOrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.in.rest.mapper.OrderMapper;
import com.galega.order.adapters.out.notification.sns.enums.ReturnTypes;
import com.galega.order.adapters.out.notification.sns.handler.SNSOutHandler;
import com.galega.order.adapters.out.notification.sns.mapper.SNSOrderOutMapper;
import com.galega.order.domain.entity.Order;
import com.galega.order.domain.enums.OrderStatus;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.service.OrderService;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Component
public class SQSInHandler extends BaseSQSHandler {

	@Autowired
	SNSOutHandler snsOutHandler;

	@Autowired
	AppConfig appConfig;

	private final int MAX_NUMBER_MESSAGES = 10;
	private final int WAIT_TIME_SECONDS = 20;
	private final IOrderUseCase orderUseCase;

	public SQSInHandler(DataSource dataSource) {
		this.orderUseCase = new OrderService(dataSource);
	}

	@Scheduled(fixedDelay = 5000)
	public void listenToQueue() {
		try {
			logger.info("appConfig.getSqsQueueUrl(): {}", appConfig.getSqsQueueUrl());
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
					.queueUrl(appConfig.getSqsQueueUrl())
					.maxNumberOfMessages(MAX_NUMBER_MESSAGES)
					.messageAttributeNames("messageType")
					.waitTimeSeconds(WAIT_TIME_SECONDS)
					.build();

			ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
			List<Message> messages = receiveMessageResponse.messages();

			for (Message message : messages) {
				handleMessage(message);
				sqsClient.deleteMessage(builder -> builder.queueUrl(appConfig.getSqsQueueUrl()).receiptHandle(message.receiptHandle()).build());
			}

		} catch (Exception e) {
			logger.error("Error while receiving messages from SQS queue: {}", appConfig.getSqsQueueUrl(), e);
		}
	}

	private void handleMessage(Message message) throws IllegalArgumentException {
		String messageBody = message.body();
		logger.info("Received message: {}", messageBody);

		var messageAttribute = message.messageAttributes();
		if (messageAttribute == null){
			logger.error("Message attribute is invalid");
			throw new IllegalArgumentException("Message attribute is invalid");
		}

		logger.info("messageAttribute: {}", messageAttribute);
		var messageType = messageAttribute.get("messageType");
		if (messageType == null) {
			logger.error("Message Type is null");
			throw new IllegalArgumentException("Message Type is null");
		}

		var messageTypeValue = messageType.stringValue();
		if (StringUtils.isEmpty(messageTypeValue)) {
			logger.error("Message type is empty");
			throw new IllegalArgumentException("Message type is empty");
		}

		if (OperationTypes.UPDATE_ORDER_STATUS.toString().equals(messageTypeValue)) {
			logger.info("UPDATE_ORDER_STATUS");
			processUpdateOrderStatus(messageBody);
		} else if (OperationTypes.CREATE_ORDER.toString().equals(messageTypeValue)) {
			logger.info("CREATE_ORDER");
			processCreateOrder(messageBody);
		} else {
			logger.error("Message type {} is invalid", messageTypeValue);
			throw new IllegalArgumentException("Message type " + messageTypeValue + " is invalid");
		}
	}

	private void processCreateOrder(String messageBody) {
		CreateOrderDTO request = SQSOrderInMapper.mapCreateOrderDTO(messageBody);
		Order order = OrderMapper.toDomain(request);

		try {
			Order createdOrder = orderUseCase.create(order);

			if (createdOrder != null) {
				logger.info("Order created successfully with ID: {}", createdOrder.getId());
				var orderDTO = new OrderDTO(createdOrder);
				var orderMessage = SNSOrderOutMapper.orderDTOtoJson(orderDTO);
				snsOutHandler.sendMessage(orderMessage, ReturnTypes.ORDER_CREATED);
			} else {
				logger.warn("Failed to create order from request: {}", request);
			}

		} catch (Exception e) {
			logger.error("Error creating order from request: {}", request, e);
		}
	}

	private void processUpdateOrderStatus(String messageBody) {
		UpdateOrderStatusDTO request = SQSOrderInMapper.mapUpdateOrderStatusDTO(messageBody);
		UUID orderId = UUID.fromString(request.getId());
		OrderStatus status = OrderStatus.fromString(request.getStatus().toUpperCase());

		try {
			boolean updated = orderUseCase.updateStatus(orderId, status);
			if (updated) {
				logger.info("Order status updated successfully for ID: {}", orderId);
				snsOutHandler.sendMessage(Boolean.toString(true), ReturnTypes.ORDER_STATUS_UPDATED);
			} else {
				logger.warn("Failed to update status for order ID: {}", orderId);
			}
		} catch (OrderAlreadyWithStatusException | EntityNotFoundException e) {
			logger.error("Error updating order status for ID: {}", orderId, e);
		}
	}
}
