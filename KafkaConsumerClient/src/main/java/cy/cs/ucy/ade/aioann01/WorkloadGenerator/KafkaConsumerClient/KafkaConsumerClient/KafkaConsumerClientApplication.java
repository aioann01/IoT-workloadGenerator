package cy.cs.ucy.ade.aioann01.WorkloadGenerator.KafkaConsumerClient.KafkaConsumerClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaConsumerClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaConsumerClientApplication.class, args);
		KafkaConsumerClient kafkaConsumerClient=new KafkaConsumerClient("kafka-broker","9092");
	}

}
