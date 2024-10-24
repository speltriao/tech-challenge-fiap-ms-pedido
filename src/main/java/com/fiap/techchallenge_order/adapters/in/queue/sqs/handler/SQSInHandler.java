package com.fiap.techchallenge_order.adapters.in.queue.sqs.handler;

import com.fiap.techchallenge_order.adapters.BaseSQSHandler;
import com.fiap.techchallenge_order.adapters.in.queue.sqs.dto.UpdateOrderStatusDTO;
import com.fiap.techchallenge_order.adapters.in.queue.sqs.enums.OperationTypes;
import com.fiap.techchallenge_order.adapters.in.queue.sqs.mapper.SQSOrderInMapper;
import com.fiap.techchallenge_order.adapters.in.rest.dto.CreateOrderDTO;
import com.fiap.techchallenge_order.adapters.in.rest.dto.OrderDTO;
import com.fiap.techchallenge_order.adapters.in.rest.mapper.OrderMapper;
import com.fiap.techchallenge_order.adapters.out.queue.sqs.enums.ReturnTypes;
import com.fiap.techchallenge_order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.fiap.techchallenge_order.adapters.out.queue.sqs.mapper.SQSOrderOutMapper;
import com.fiap.techchallenge_order.domain.entity.Order;
import com.fiap.techchallenge_order.domain.enums.OrderStatus;
import com.fiap.techchallenge_order.domain.exception.EntityNotFoundException;
import com.fiap.techchallenge_order.domain.exception.OrderAlreadyWithStatusException;
import com.fiap.techchallenge_order.domain.service.OrderService;
import com.fiap.techchallenge_order.domain.usecase.IOrderUseCase;
import org.apache.commons.lang3.StringUtils;
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

	private final int MAX_NUMBER_MESSAGES = 10;
	private final int WAIT_TIME_SECONDS = 20;
	private final IOrderUseCase orderUseCase;

	public SQSInHandler(DataSource dataSource) {
		this.orderUseCase = new OrderService(dataSource);
	}

	@Autowired
	SQSOutHandler sqsOutHandler;

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
				var orderMessage = SQSOrderOutMapper.orderDTOtoJson(orderDTO);
				sqsOutHandler.sendMessage(orderMessage, ReturnTypes.ORDER_CREATED) ;
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
				sqsOutHandler.sendMessage(Boolean.toString(true), ReturnTypes.ORDER_STATUS_UPDATED) ;
			} else {
				logger.warn("Failed to update status for order ID: {}", orderId);
			}
		} catch (OrderAlreadyWithStatusException | EntityNotFoundException e) {
			logger.error("Error updating order status for ID: {}", orderId, e);
		}
	}
}
