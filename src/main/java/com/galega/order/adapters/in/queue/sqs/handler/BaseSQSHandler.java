package com.galega.order.adapters.in.queue.sqs.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;


public abstract class BaseSQSHandler {
	@Value("${aws.sqs.queueUrl}")
	protected String queueUrl;

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final SqsClient sqsClient;


	public BaseSQSHandler() {
		if (queueUrl == null || queueUrl.isEmpty()) {
			throw new IllegalArgumentException("aws.sqs.queueUrl environment variable is not set or is empty");
		}

		this.sqsClient = SqsClient.builder()
				.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
				.build();
		logger.info("SQS client initialized with queue URL: {}", queueUrl);
	}
}
