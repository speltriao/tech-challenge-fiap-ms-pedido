package com.galega.order.adapters.out.notification.sns.handler;

import com.galega.order.adapters.AppConfig;
import com.galega.order.adapters.out.notification.sns.enums.ReturnTypes;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;

@Component
public class SNSOutHandler {

	@Autowired
	private AppConfig appConfig;

	private SnsClient snsClient;

	private static final Logger logger = LoggerFactory.getLogger(SNSOutHandler.class);

	@PostConstruct
	private void init() {
		String region = appConfig.getRegion();
		logger.info("Using region: {}", region);  // Log the region to ensure it is correct

		AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(
				appConfig.getAccessKeyId(),
				appConfig.getSecretKey(),
				appConfig.getSessionToken()
		));

		this.snsClient = SnsClient.builder()
				.credentialsProvider(credentialsProvider)
				.region(Region.of(appConfig.getRegion()))
				.build();

		logger.info("SNS client initialized with topic ARN: {}", appConfig.getSnsTopicArn());
	}

	public void sendMessage(String messageBody, ReturnTypes returnType) {
		try {
			PublishRequest publishRequest = PublishRequest.builder()
					.topicArn(appConfig.getSnsTopicArn())
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
			logger.error("Failed to send notification to SNS topic: {}", appConfig.getSnsTopicArn(), e);
		}
	}
}
