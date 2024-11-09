package com.fiap.techchallenge_order.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;


public abstract class BaseSQSHandler {
	private final String SQS_QUEUE_ENV = "SQS_QUEUE_URL";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final SqsClient sqsClient;
	protected final String queueUrl = System.getenv(SQS_QUEUE_ENV);


	public BaseSQSHandler() {
		if (queueUrl == null || queueUrl.isEmpty()) {
			throw new IllegalArgumentException("SQS_QUEUE_URL environment variable is not set or is empty");
		}

		this.sqsClient = SqsClient.builder()
				.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
				.build();
		logger.info("SQS client initialized with queue URL: {}", queueUrl);
	}
}
