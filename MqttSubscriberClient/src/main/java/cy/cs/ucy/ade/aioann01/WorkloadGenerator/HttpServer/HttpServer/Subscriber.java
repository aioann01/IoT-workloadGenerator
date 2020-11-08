package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer;

import org.eclipse.paho.client.mqttv3.*;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Subscriber implements MqttCallback {

    private final int qos = 1;
    private static final String PROXIMITY_SENSOR_TOPIC = "home/proximity_sensor/#";
    private static final String MOTION_DETECTION_SENSOR_TOPIC = "home/motion_detection_sensor/#";
    private static final String HUMIDITY_SENSOR_TOPIC = "home/humidity_sensor/#";
    private static final String  TEMPERATURE_SENSOR_TOPIC= "home/temperature_sensor/#";
    private static final String PRESSURE_SENSOR_TOPIC = "home/pressure_sensor/#";
    private static final String DUMMY_SENSOR_SENSOR_TOPIC = "home/dummy_sensor/#";
    private static final String TESTING_SENSOR_SENSOR_TOPIC = "home/testing_sensor/#";

    private static final Logger log= LoggerFactory.getLogger(Subscriber.class);


    private MqttClient client;

    public Subscriber(String uri) throws MqttException, URISyntaxException {
        this(new URI(uri));
    }

    public Subscriber(URI uri) throws MqttException {
        String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());
      //  String[] auth = this.getAuth(uri);
      //  String username = auth[0];
       // String password = auth[1];
        String clientId = MqttAsyncClient.generateClientId();
//        if (!uri.getPath().isEmpty()) {
//            this.topic = uri.getPath().substring(1);
//        }

        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
      //  conOpt.setUserName(username);
     //   conOpt.setPassword(password.toCharArray());

        this.client = new MqttClient(host, clientId);
        this.client.setCallback(this);
        this.client.connect(conOpt);

//        this.client.subscribe(PROXIMITY_SENSOR_TOPIC, qos);
//        this.client.subscribe(HUMIDITY_SENSOR_TOPIC, qos);
//        this.client.subscribe(TEMPERATURE_SENSOR_TOPIC, qos);
//        this.client.subscribe(TESTING_SENSOR_SENSOR_TOPIC, qos);
//        this.client.subscribe(DUMMY_SENSOR_SENSOR_TOPIC, qos);
//        this.client.subscribe(MOTION_DETECTION_SENSOR_TOPIC, qos);
//        this.client.subscribe(PRESSURE_SENSOR_TOPIC, qos);
        this.client.subscribe("home/#", qos);



    }

    private String[] getAuth(URI uri) {
        String a = uri.getAuthority();
        String[] first = a.split("@");
        return first[0].split(":");
    }

//    public void sendMessage(String payload) throws MqttException {
//        MqttMessage message = new MqttMessage(payload.getBytes());
//        message.setQos(qos);
//        this.client.publish(this.topic, message); // Blocking publish
//    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        log.error("Connection lost because: " + cause);
        System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        log.info(String.format("[%s] %s", topic, new String(message.getPayload())));
    }


//        public static void main(String [] args) throws MqttException, URISyntaxException {
//        Subscriber s = new Subscriber("https://mqtt_broker_ms:1883");
////        s.sendMessage("Hello");
////        s.sendMessage("Hello 2");
//    }
}