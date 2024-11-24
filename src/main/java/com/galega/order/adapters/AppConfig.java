package com.galega.order.adapters;

import lombok.Getter;
import org.springframework.stereotype.Component;


@Component
public class AppConfig {
	public final String region = System.getenv("AWS_REGION");
	public final String sqsOutputQueueUrl = System.getenv("AWS_SQS_OUTPUT_QUEUE_URL");; //Fila onde as mensagens serão publicadas
	public final String sqsInputQueueUrl= System.getenv("AWS_SQS_INPUT_QUEUE_URL"); //Fila que escuta o SNS
	public final String accessKeyId= System.getenv("AWS_ACCESS_KEY_ID");
	public final String secretKey= System.getenv("AWS_SECRET_ACCESS_KEY");
	public final String sessionToken= System.getenv("AWS_SESSION_TOKEN");
}
