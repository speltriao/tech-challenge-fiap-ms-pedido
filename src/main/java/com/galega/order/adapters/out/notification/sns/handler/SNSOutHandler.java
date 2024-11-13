package com.galega.order.adapters.out.notification.sns.handler;

import com.galega.order.adapters.out.notification.sns.enums.ReturnTypes;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SNSOutHandler {

	private final SnsClient snsClient;
	private final String topicArn;
	private static final Logger logger = LoggerFactory.getLogger(SNSOutHandler.class);

	public SNSOutHandler(SnsClient snsClient, String topicArn) {
		this.snsClient = snsClient;
		this.topicArn = topicArn;
	}

	public void sendMessage(String messageBody, ReturnTypes returnType) {
		try {
			PublishRequest publishRequest = PublishRequest.builder()
					.topicArn(topicArn)
					.message(messageBody)
					.messageAttributes(Map.of(
							"messageType", MessageAttributeValue.builder()
									.dataType("String")
									.stringValue(returnType.toString())
									.build()
					))
					.build();

			PublishResponse publishResponse = snsClient.publish(publishRequest);
			logger.info("Notification sent successfully with ID: {}", publishResponse.messageId());

		} catch (Exception e) {
			logger.error("Failed to send notification to SNS topic: {}", topicArn, e);
		}
	}
}
