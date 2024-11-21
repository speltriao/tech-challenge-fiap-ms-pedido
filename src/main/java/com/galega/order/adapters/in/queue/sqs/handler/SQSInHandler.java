package com.galega.order.adapters.in.queue.sqs.handler;

import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.in.queue.sqs.dto.UpdateOrderStatusDTO;
import com.galega.order.adapters.in.queue.sqs.mapper.SQSOrderInMapper;
import com.galega.order.adapters.out.queue.sqs.enums.ReturnTypes;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.enums.OrderStatus;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.service.OrderService;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Component
public class SQSInHandler extends BaseSQSHandler {

	@Autowired
	SQSOutHandler SQSOutHandler;

	private final int MAX_NUMBER_MESSAGES = 10;
	private final int WAIT_TIME_SECONDS = 20;
	private final IOrderUseCase orderUseCase;

	public SQSInHandler(DataSource dataSource) {
		this.orderUseCase = new OrderService(dataSource);
	}

	@Scheduled(fixedDelay = 5000)
	public void listenToQueue() {
		try {
			logger.info("appConfig.getSqsQueueUrl(): {}", appConfig.getSqsInputQueueUrl());
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
					.queueUrl(appConfig.getSqsInputQueueUrl())
					.maxNumberOfMessages(MAX_NUMBER_MESSAGES)
					.waitTimeSeconds(WAIT_TIME_SECONDS)
					.build();

			ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
			List<Message> messages = receiveMessageResponse.messages();

			for (Message message : messages) {
				handleMessage(message);
				sqsClient.deleteMessage(builder -> builder.queueUrl(appConfig.getSqsInputQueueUrl()).receiptHandle(message.receiptHandle()).build());
			}

		} catch (Exception e) {
			logger.error("Error while receiving messages from SQS queue: {}", appConfig.getSqsInputQueueUrl(), e);
		}
	}

	private void handleMessage(Message message) throws IllegalArgumentException {
		String messageBody = message.body();
		logger.info("Received message: {}", messageBody);
		processUpdateOrderStatus(messageBody);
	}

	private void processUpdateOrderStatus(String messageBody) {
		UpdateOrderStatusDTO request = SQSOrderInMapper.mapUpdateOrderStatusDTO(messageBody);
		UUID orderId = UUID.fromString(request.getId());
		OrderStatus status = OrderStatus.fromString(request.getStatus().toUpperCase());

		try {
			boolean updated = orderUseCase.updateStatus(orderId, status);
			if (updated) {
				logger.info("Order status updated successfully for ID: {}", orderId);
				SQSOutHandler.sendMessage(orderId, status);
			} else {
				logger.warn("Failed to update status for order ID: {}", orderId);
			}
		} catch (OrderAlreadyWithStatusException | EntityNotFoundException e) {
			logger.error("Error updating order status for ID: {}", orderId, e);
		}
	}
}
