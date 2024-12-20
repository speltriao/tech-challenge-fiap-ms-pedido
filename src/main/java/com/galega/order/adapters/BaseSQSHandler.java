package com.galega.order.adapters;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public abstract class BaseSQSHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected SqsClient sqsClient;

	public BaseSQSHandler() {}

	@PostConstruct
	private void init() {
		this.sqsClient = SqsClient.builder()
				.credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(
						AppConfig.accessKeyId,
						AppConfig.secretKey,
						AppConfig.sessionToken
				)))
				.region(Region.of(AppConfig.region))
				.build();
	}
}
