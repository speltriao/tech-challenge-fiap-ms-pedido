package com.galega.order.adapters.in.queue.sqs.handler;

import com.galega.order.adapters.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public abstract class BaseSQSHandler {

	@Autowired
	private AppConfig appConfig;

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected SqsClient sqsClient;

	public BaseSQSHandler() {
	}

	@PostConstruct
	private void init() {
		this.sqsClient = SqsClient.builder()
				.credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(
						appConfig.getAccessKeyId(),
						appConfig.getSecretKey(),
						appConfig.getSessionToken()
				)))
				.region(Region.of(appConfig.getRegion()))
				.build();

		logger.info("SQS client initialized with queue URL: {}", appConfig.getSqsQueueUrl());
	}
}
