package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Server;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorMessageSendService;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Service
public class KafkaSensorMessageSendService implements ISensorMessageSendService {

    public static Integer MESSAGE_COUNT = 1000;
    public static String CLIENT_ID = "client1";
    public static String TOPIC_NAME = "home";
    public static String GROUP_ID_CONFIG = "consumerGroup1";
    public static Integer MAX_NO_MESSAGE_FOUND_COUNT = 100;
    public static String OFFSET_RESET_LATEST = "latest";
    public static String OFFSET_RESET_EARLIER = "earliest";
    public static Integer MAX_POLL_RECORDS = 1;
    private List<Server> kafkaBrokers;
    private String rootTopic;
    private Properties properties;

    private static final Logger log = LoggerFactory.getLogger(KafkaSensorMessageSendService.class);

    private List<Producer<String, String>> kafkaProducerClients;

    @Override
    public void initializeServiceReceiverConfigurations(List<Server> kafkaBrokers, HashMap<String, String> configs) throws Exception {
        this.kafkaBrokers = kafkaBrokers;
        for (Server broker : kafkaBrokers) {
            this.rootTopic = configs.get(TOPIC_NAME);
            this.properties = new Properties();
            this.properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getServerIp() + ":" + broker.getServerPort());
            this.properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
            this.properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            this.properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            //properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
            kafkaProducerClients.add(new KafkaProducer<>(properties));
        }
    }

    @Override
    public void sendMessage(String sensorId, String message, SensorMessageEnum contentType) throws Exception {
        for (int i = 0; i < kafkaProducerClients.size(); ++i) {
            Producer kafkaProducerClient = kafkaProducerClients.get(0);
            try {
                log.debug("Sending request to Kafka broker {" + kafkaBrokers.get(i).getServerIp() + " for sensor {" + sensorId + "} :" + message);

                final Boolean messageSuccesffullySent = false;
                if (rootTopic == null) {
                    kafkaProducerClient.send(new ProducerRecord<String, String>(TOPIC_NAME,
                            sensorId, message), new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null) {
                                log.error("Exception caught while sending request to KAFKA broker :" + "for sensor {" + sensorId + "+} :" + e.getMessage(), e);
                            } else {
                                log.debug("Message was succesfully sent to MQTT broker");
                            }
                        }
                    });
                } else {
                    kafkaProducerClient.send(new ProducerRecord<String, String>(TOPIC_NAME,
                            rootTopic + "/" + sensorId, message), new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null) {
                                log.error("Exception caught while sending request to KAFKA broker :" + "for sensor {" + sensorId + "+} :" + e.getMessage(), e);
                            } else {
                                log.debug("Message was succesfully sent to MQTT broker");
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Exception caught while sending request to KAFKA broker :" + this.properties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG) + "for sensor {" + sensorId + "+} :" + e.getMessage(), e);
                throw new Exception("Could not send message to KAFKA broker due to:" + e.getMessage());
            }
        }
    }

    @Override
    public void terminate() throws Exception {
        for (Producer kafkaProducerClient : kafkaProducerClients) {
            kafkaProducerClient.close();
        }
        log.info("Connection with kafka broker succesfully  closed.");
    }


}
