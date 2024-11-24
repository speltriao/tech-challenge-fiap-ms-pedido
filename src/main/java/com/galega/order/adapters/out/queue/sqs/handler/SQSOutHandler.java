package com.galega.order.adapters.out.queue.sqs.handler;

import com.galega.order.adapters.AppConfig;
import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.out.queue.sqs.mapper.OrderOutputMapper;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;

@Component
public class SQSOutHandler extends BaseSQSHandler {

	public void sendOrderMessage(OrderDTO order) {
		try {
			SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
					.queueUrl(AppConfig.sqsOutputQueueUrl)
					.messageBody(OrderOutputMapper.orderDTOtoJson(order))
					.messageAttributes(Map.of(
							"messageType", MessageAttributeValue.builder()
									.dataType("String")
									.stringValue(order.getStatus().toString())
									.build()
					))
					.build();

			SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);

			logger.info("Message sent successfully to SQS with ID: {}", response.messageId());
		} catch (Exception e) {
			logger.error("Failed to send message to SQS queue: {}", AppConfig.sqsOutputQueueUrl, e);
		}
	}
}
