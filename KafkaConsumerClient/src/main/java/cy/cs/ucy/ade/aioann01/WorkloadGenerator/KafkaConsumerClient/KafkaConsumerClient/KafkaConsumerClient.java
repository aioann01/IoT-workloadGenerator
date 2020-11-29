package cy.cs.ucy.ade.aioann01.WorkloadGenerator.KafkaConsumerClient.KafkaConsumerClient;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class KafkaConsumerClient {
    private static final String PROXIMITY_SENSOR_TOPIC = "home/proximity_sensor";
    private static final String MOTION_DETECTION_SENSOR_TOPIC = "home/motion_detection_sensor";
    private static final String HUMIDITY_SENSOR_TOPIC = "home/humidity_sensor";
    private static final String TEMPERATURE_SENSOR_TOPIC = "home/temperature_sensor";
    private static final String PRESSURE_SENSOR_TOPIC = "home/pressure_sensor";
    private static final String DUMMY_SENSOR_SENSOR_TOPIC = "home/dummy_sensor";
    private static final String TESTING_SENSOR_SENSOR_TOPIC = "home/testing_sensor";
    private List<String> topics;
    {
        topics = new ArrayList<>();
       // topics.add("home/#");
        topics.add("home");

//        topics.add(PROXIMITY_SENSOR_TOPIC);
//        topics.add(MOTION_DETECTION_SENSOR_TOPIC);
//        topics.add(HUMIDITY_SENSOR_TOPIC);
//        topics.add(TEMPERATURE_SENSOR_TOPIC);
//        topics.add(PRESSURE_SENSOR_TOPIC);
//        topics.add(DUMMY_SENSOR_SENSOR_TOPIC);
//        topics.add(TESTING_SENSOR_SENSOR_TOPIC);

    }
    //KAFKA constants
    public static String KAFKA_BROKERS = "127.0.0.1:9092";
    public static Integer MESSAGE_COUNT=1000;
    public static String CLIENT_ID="client1";
    public static String TOPIC_NAME="home";
    public static String GROUP_ID_CONFIG="consumerGroup1";
    public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    public static String OFFSET_RESET_LATEST="latest";
    public static String OFFSET_RESET_EARLIER="earliest";
    public static Integer MAX_POLL_RECORDS=1;
    private static final Logger log= LoggerFactory.getLogger(KafkaConsumerClient.class);

    public KafkaConsumerClient(String ip,String port){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,ip+":"+port );
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer
                <String, String>(properties);
        consumer.subscribe(topics);

        while (true){
            ConsumerRecords<String,String> records=consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)

                // print the offset,key and value for the consumer records.
                System.out.printf("offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value());
        }

    }


}
