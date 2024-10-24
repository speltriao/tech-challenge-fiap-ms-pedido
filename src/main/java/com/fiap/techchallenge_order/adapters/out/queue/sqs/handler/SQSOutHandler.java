package com.fiap.techchallenge_order.adapters.out.queue.sqs.handler;

import com.fiap.techchallenge_order.adapters.BaseSQSHandler;
import com.fiap.techchallenge_order.adapters.out.queue.sqs.enums.ReturnTypes;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;

@Component
public class SQSOutHandler extends BaseSQSHandler {

	public void sendMessage(String messageBody, ReturnTypes returnType) {
		try {
			SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
					.queueUrl(queueUrl)
					.messageBody(messageBody)
					.messageAttributes(Map.of(
							"messageType", MessageAttributeValue.builder()
									.dataType("String")
									.stringValue(returnType.toString())
									.build()
					))
					.build();

			SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);
			logger.info("Message sent successfully with ID: {}", sendMessageResponse.messageId());

		} catch (Exception e) {
			logger.error("Failed to send message to SQS queue: {}", queueUrl, e);
		}
	}
}
