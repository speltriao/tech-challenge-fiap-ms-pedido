package com.galega.order.adapters.out.notification.sns.handler;

import com.galega.order.adapters.out.notification.sns.enums.ReturnTypes;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

	private SnsClient snsClient;

	@Value("${aws.sns.topicArn}")
	private String topicArn;

	@Value("${aws.accessKeyId}")
	private String accessKeyId;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.sessionToken}")
	private String sessionToken;

	@Value("${aws.region}")
	private String region;

	private static final Logger logger = LoggerFactory.getLogger(SNSOutHandler.class);

	public SNSOutHandler() {
		// The constructor should be lightweight, and the properties will be set later by Spring.
		this.snsClient = null; // Initialize snsClient to null; it will be properly initialized later.
	}

	@PostConstruct
	public void init() {
		// Ensure the property values are set and non-empty
		if (topicArn == null || topicArn.trim().isEmpty()) {
			throw new IllegalArgumentException("aws.sns.topicArn property is not set or is empty");
		}

		if (accessKeyId == null || accessKeyId.trim().isEmpty() ||
				secretKey == null || secretKey.trim().isEmpty() ||
				sessionToken == null || sessionToken.trim().isEmpty() ||
				region == null || region.trim().isEmpty()) {
			throw new IllegalArgumentException("AWS credentials or region are not set in properties");
		}

		AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(
				accessKeyId,
				secretKey,
				sessionToken
		));

		this.snsClient = SnsClient.builder()
				.credentialsProvider(credentialsProvider)
				.region(Region.of(region))
				.build();

		logger.info("SNS client initialized with topic ARN: {}", topicArn);
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
