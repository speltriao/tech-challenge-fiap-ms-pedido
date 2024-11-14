package com.galega.order.adapters.in.queue.sqs.handler;


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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
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

	@Value("${aws.sqs.queueUrl}")
	protected String queueUrl;

	@Value("${aws.accessKeyId}")
	private String accessKeyId;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.sessionToken}")
	private String sessionToken;

	@Value("${aws.region}")
	private String region;

	private final int MAX_NUMBER_MESSAGES = 10;
	private final int WAIT_TIME_SECONDS = 20;
	private final IOrderUseCase orderUseCase;
	private final SqsClient sqsClient;


	public SQSInHandler(DataSource dataSource) {
		this.orderUseCase = new OrderService(dataSource);

		this.sqsClient = SqsClient.builder()
				.credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(
						accessKeyId,
						secretKey,
						sessionToken
				)))
				.region(Region.of(this.region))
				.build();
	}


	@Scheduled(fixedDelay = 5000)
	public void listenToQueue() {
		try {
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
					.queueUrl(queueUrl)
					.maxNumberOfMessages(MAX_NUMBER_MESSAGES)
					.waitTimeSeconds(WAIT_TIME_SECONDS)
					.build();

			ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
			List<Message> messages = receiveMessageResponse.messages();

			for (Message message : messages) {
				handleMessage(message);
				sqsClient.deleteMessage(builder -> builder.queueUrl(queueUrl).receiptHandle(message.receiptHandle()).build());
			}

		} catch (Exception e) {
			logger.error("Error while receiving messages from SQS queue: {}", queueUrl, e);
		}
	}

	private void handleMessage(Message message) throws  IllegalArgumentException {
		String messageBody = message.body();
		logger.info("Received message: {}", messageBody);
		String messageType = message.messageAttributes().get("messageType").stringValue();
		logger.info("Received message with type: {}", messageType);

		if (StringUtils.isEmpty(messageType)){
			logger.error("Message type is empty");
			throw new IllegalArgumentException("Message type is empty");
		}

		if (OperationTypes.UPDATE_ORDER_STATUS.toString().equals(messageType)){
			processUpdateOrderStatus(messageBody);
		}
		else if (OperationTypes.CREATE_ORDER.toString().equals(messageType)){
			processCreateOrder(messageBody);
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
				snsOutHandler.sendMessage(orderMessage, ReturnTypes.ORDER_CREATED) ;
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
				snsOutHandler.sendMessage(Boolean.toString(true), ReturnTypes.ORDER_STATUS_UPDATED) ;
			} else {
				logger.warn("Failed to update status for order ID: {}", orderId);
			}
		} catch (OrderAlreadyWithStatusException | EntityNotFoundException e) {
			logger.error("Error updating order status for ID: {}", orderId, e);
		}
	}
}
