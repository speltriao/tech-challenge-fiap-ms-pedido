package com.galega.order.adapters;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppConfig {

	private final String region = System.getenv("AWS_REGION");
	private final String sqsOutputQueueUrl = System.getenv("AWS_SQS_OUTPUT_QUEUE_URL");; //Fila onde as mensagens serão publicadas
	private final String sqsInputQueueUrl= System.getenv("AWS_SQS_INPUT_QUEUE_URL"); //Fila que escuta o SNS
	private final String accessKeyId= System.getenv("AWS_ACCESS_KEY_ID");
	private final String secretKey= System.getenv("AWS_SECRET_ACCESS_KEY");
	private final String sessionToken= System.getenv("AWS_SESSION_TOKEN");
}
