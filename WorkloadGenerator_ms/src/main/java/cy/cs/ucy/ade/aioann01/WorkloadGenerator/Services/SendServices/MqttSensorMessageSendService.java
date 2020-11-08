package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Server;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorMessageSendService;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.EXCEPTION_CAUGHT;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.TOPIC_NAME;

@Service
public class MqttSensorMessageSendService implements ISensorMessageSendService {


    List<IMqttClient> mqttClients = new ArrayList<>();

    private List<Server> mqttBrokers;

    private String rootTopic;

    private static final Logger log = LoggerFactory.getLogger(MqttSensorMessageSendService.class);


    @Override
    public void initializeServiceReceiverConfigurations(List<Server> mqttBrokers, HashMap<String, String> configs) throws Exception {
        this.mqttBrokers = mqttBrokers;
        this.rootTopic = configs.get(TOPIC_NAME);
        for (Server mqttBroker : mqttBrokers) {
            try {
                String clientId = MqttAsyncClient.generateClientId();
                MqttClient mqttClient = new MqttClient("tcp://" + mqttBroker.getServerIp() + ":" + mqttBroker.getServerPort(), clientId);
                mqttClients.add(mqttClient);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);
                options.setConnectionTimeout(10);
                options.setMaxInflight(1000);
                mqttClient.connect(options);
            } catch (MqttException mqttException) {
                log.error("Error while initializing configurations for SensorMessageSendService for MQTT Protocol due to:" + mqttException.getMessage(), mqttException);
                throw new Exception("Could not initialize MQTT Publisher Client due to:" + mqttException.getMessage());
            }
        }

    }

    @Override
    public void sendMessage(String sensorId, String message, SensorMessageEnum contentType) throws Exception {

        for (IMqttClient mqttClient : mqttClients) {
            try {
                if (!mqttClient.isConnected())
                    return;
                byte[] payload = String.format("%s", message).getBytes();
                MqttMessage msg = new MqttMessage(payload);
                msg.setQos(0);
                msg.setRetained(true);
                if (rootTopic == null)
                    mqttClient.publish(sensorId, msg);
                else
                    mqttClient.publish(rootTopic + "/" + sensorId, msg);

            } catch (Exception e) {
                log.error("Exception caught while sending request to MQTT server: " + mqttBrokers.get(0).getServerIp() + ":" + mqttBrokers.get(0).getServerPort() + " for sensor {" + sensorId + "+} :" + e.getMessage(), e);
                throw new Exception("Could not send message to MQTT server due to :" + e.getMessage());
            }
        }
    }

    @Override
    public void terminate() throws Exception {
        for (IMqttClient mqttClient : mqttClients) {
            mqttClient.disconnect();
            mqttClient.close();
            log.info("Connection with MQTT broker closed.");
        }
    }

}
