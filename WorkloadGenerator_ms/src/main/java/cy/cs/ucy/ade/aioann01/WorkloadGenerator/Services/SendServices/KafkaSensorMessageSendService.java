package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Server;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface.ISensorMessageSendService;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.EXCEPTION_CAUGHT;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.KAFKA_BROKER_CLUSTERS;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.TOPIC;

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

    private ObjectMapper mapper = new ObjectMapper();

    private List<Producer<String, String>> kafkaProducerClients;


    @Override
    public void validateAndProcessConfigs(JSONObject protocolConfigs) throws ValidationException, Exception {
        String errorMessage = "";
        if (protocolConfigs.optString(TOPIC) != null) {
            this.rootTopic = protocolConfigs.optString(TOPIC);
        } else{
            errorMessage = "No root topic was provided in protocolConfigs";
            throw new ValidationException(errorMessage);
        }
        if (protocolConfigs.has(KAFKA_BROKER_CLUSTERS)) {
            try {
                JSONArray kafkaBrokersJsonArray = protocolConfigs.getJSONArray(KAFKA_BROKER_CLUSTERS);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                kafkaBrokers = mapper.readValue(kafkaBrokersJsonArray.toString(), new TypeReference<List<Server>>() {
                });
                if (kafkaBrokers == null || kafkaBrokers.isEmpty()) {
                    errorMessage = " kafkaBrokers could not be created.Check logs or verify that file has valid kafkaBrokersClusters";
                    throw new Exception(errorMessage);
                }
            } catch (JSONException | JsonMappingException exception) {
                errorMessage = " Couldn't parse  kafkaBrokers from config file";
                log.error(EXCEPTION_CAUGHT + errorMessage + "-" + exception.getMessage(), exception);
                throw new Exception(errorMessage);
            }
        }
        else {
            errorMessage = " No kafkaBrokers configs was provided for protocol KAFKA";
            throw new Exception(errorMessage);
        }
    }


    @Override
    public void initializeConnections() throws Exception {
        this.kafkaProducerClients = new ArrayList<>();
        for (Server broker : kafkaBrokers) {
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
                // log.debug("Sending request to Kafka broker {" + kafkaBrokers.get(i).getServerIp() + " for sensor {" + sensorId + "} :" + message);
                final Boolean messageSuccesffullySent = false;
                kafkaProducerClient.send(new ProducerRecord<String, String>(this.rootTopic,
                        sensorId, message), new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null) {
                            log.error("Exception caught while sending request to KAFKA broker :" + "for sensor {" + sensorId + "+} :" + e.getMessage(), e);
                        } else {
                            //log.debug("Message was succesfully sent to MQTT broker");
                        }
                    }
                });

//                else {
//                    kafkaProducerClient.send(new ProducerRecord<String, String>(this.rootTopic,
//                            rootTopic + "/" + sensorId, message), new Callback() {
//                        public void onCompletion(RecordMetadata metadata, Exception e) {
//                            if (e != null) {
//                                log.error("Exception caught while sending request to KAFKA broker :" + "for sensor {" + sensorId + "+} :" + e.getMessage(), e);
//                            } else {
//                                log.debug("Message was succesfully sent to MQTT broker");
//                            }
//                        }
//                    });
//                }
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
