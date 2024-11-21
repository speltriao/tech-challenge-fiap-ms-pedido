package com.galega.order.adapters.out.queue.sqs.handler;

import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.out.queue.sqs.enums.ReturnTypes;
import com.galega.order.adapters.out.queue.sqs.mapper.OrderOutputMapper;
import com.galega.order.domain.enums.OrderStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;
import java.util.UUID;

@Component
public class SQSOutHandler extends BaseSQSHandler {

	public void sendMessage(UUID orderId, OrderStatus status) {
		try {
			SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
					.queueUrl(appConfig.getSqsOutputQueueUrl())
					.messageBody(OrderOutputMapper.orderIdtoJson(orderId))
					.messageAttributes(Map.of(
							"messageType", MessageAttributeValue.builder()
									.dataType("String")
									.stringValue(status.toString())
									.build()
					))
					.build();

			SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);

			logger.info("Message sent successfully to SQS with ID: {}", response.messageId());
		} catch (Exception e) {
			logger.error("Failed to send message to SQS queue: {}", appConfig.getSqsOutputQueueUrl(), e);
		}
	}
}
