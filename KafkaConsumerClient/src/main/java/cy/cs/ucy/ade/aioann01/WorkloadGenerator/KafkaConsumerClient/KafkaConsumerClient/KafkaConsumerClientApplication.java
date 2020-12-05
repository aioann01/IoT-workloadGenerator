package cy.cs.ucy.ade.aioann01.WorkloadGenerator.KafkaConsumerClient.KafkaConsumerClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaConsumerClientApplication {


	private static String kafkaHost;

	@Value("${kafka.host}")
	public void setKafkaHost(String kafkaHost){
		this.kafkaHost = kafkaHost;
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaConsumerClientApplication.class, args);
		KafkaConsumerClient kafkaConsumerClient = new KafkaConsumerClient(kafkaHost,"9092");
	}

}
