package com.galega.order.adapters.in.queue.sqs.handler;

import com.galega.order.adapters.AppConfig;
import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.in.queue.sqs.mapper.SQSOrderInMapper;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.service.OrderService;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import javax.sql.DataSource;
import java.util.List;

@Component
public class SQSInHandler extends BaseSQSHandler {

	@Autowired
	SQSOutHandler sqsOutHandler;

	private final int MAX_NUMBER_MESSAGES = 10;
	private final int WAIT_TIME_SECONDS = 20;

	@Autowired
	private IOrderUseCase orderUseCase;


	@Scheduled(fixedDelay = 5000)
	public void listenToQueue() {
		try {
			logger.info("appConfig.getSqsQueueUrl(): {}", AppConfig.sqsInputQueueUrl);
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
					.queueUrl(AppConfig.sqsInputQueueUrl)
					.maxNumberOfMessages(MAX_NUMBER_MESSAGES)
					.waitTimeSeconds(WAIT_TIME_SECONDS)
					.build();

			var receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
			List<Message> messages = receiveMessageResponse.messages();

			for (Message message : messages) {
				handleMessage(message);
				sqsClient.deleteMessage(builder -> builder.queueUrl(AppConfig.sqsInputQueueUrl).receiptHandle(message.receiptHandle()).build());
			}

		} catch (Exception e) {
			logger.error("Error while receiving messages from SQS queue: {}", AppConfig.sqsInputQueueUrl, e);
		}
	}

	private void handleMessage(Message message) throws IllegalArgumentException {
		var messageBody = message.body();
		logger.info("Received message: {}", messageBody);
		processOrderPayment(messageBody);
	}

	private void processOrderPayment(String messageBody) {
		var paymentRequest = SQSOrderInMapper.mapUpdateOrderStatusDTO(messageBody);
		logger.info("paymentRequestDTO: {}", paymentRequest);
		logger.info("payedAt: {}", paymentRequest.getPayedAt());
		logger.info("orderId: {}", paymentRequest.getOrderId());

		var orderId = paymentRequest.getOrderId();
		var paymentStatus = paymentRequest.getStatus();

		try {
			var updated = orderUseCase.processOrderPayment(orderId, paymentStatus);
			if (updated) {
				logger.info("Order status updated successfully for ID: {}", orderId);
			}
			else {
				logger.warn("Order: {} was NOT paid to be updated", orderId);
			}
		} catch (OrderAlreadyWithStatusException | EntityNotFoundException e) {
			logger.error("Error updating order status for ID: {}", orderId, e);
		}

	}
}