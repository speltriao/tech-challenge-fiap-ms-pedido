package com.galega.order.adapters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aws")
public class AppConfig {

	private String region; //Fila onde as mensagens serão publicadas
	private String sqsOutputQueueUrl; //Fila que escuta o SNS
	private String sqsInputQueueUrl;
	private String accessKeyId;
	private String secretKey;
	private String sessionToken;
}
