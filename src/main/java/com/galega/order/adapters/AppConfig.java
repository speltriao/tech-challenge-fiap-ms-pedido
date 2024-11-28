package com.galega.order.adapters;


public abstract class AppConfig {
	public static final String region = System.getenv("AWS_REGION");
	public static final String sqsOutputQueueUrl = System.getenv("AWS_SQS_OUTPUT_QUEUE_URL");; //Fila onde as mensagens serão publicadas
	public static final String sqsInputQueueUrl= System.getenv("AWS_SQS_INPUT_QUEUE_URL"); //Fila que escuta o SNS
	public static final String accessKeyId= System.getenv("AWS_ACCESS_KEY_ID");
	public static final String secretKey= System.getenv("AWS_SECRET_ACCESS_KEY");
	public static final String sessionToken= System.getenv("AWS_SESSION_TOKEN");
}
