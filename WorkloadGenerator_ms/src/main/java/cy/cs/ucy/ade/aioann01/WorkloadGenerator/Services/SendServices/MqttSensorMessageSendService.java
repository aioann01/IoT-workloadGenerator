package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Server;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorMessageSendService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.EXCEPTION_CAUGHT;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;

@Service
public class MqttSensorMessageSendService implements ISensorMessageSendService {


    List<IMqttClient> mqttClients = new ArrayList<>();

    private List<Server> mqttBrokers;

    private String rootTopic;

    private static final Logger log = LoggerFactory.getLogger(MqttSensorMessageSendService.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void validateAndProcessConfigs(JSONObject protocolConfigs) throws ValidationException, Exception {
        String errorMessage = "";
        if (protocolConfigs.optString(TOPIC) != null) {
            rootTopic = protocolConfigs.optString(TOPIC);
        } else
            log.warn("No root topic was provided. Default root topic is none");
        if (protocolConfigs.has(MQTT_BROKER_CLUSTERS)) {
            try {
                JSONArray mqttBrokersJsonArray = protocolConfigs.getJSONArray(MQTT_BROKER_CLUSTERS);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                this.mqttBrokers = mapper.readValue(mqttBrokersJsonArray.toString(), new TypeReference<List<Server>>() {
                });
                if (mqttBrokers == null || mqttBrokers.isEmpty()) {
                    errorMessage = " mqttBrokerServers could not be created.Check logs or verify that file has valid mqttBrokerServers";
                    throw new Exception(errorMessage);
                }
            } catch (JSONException | JsonMappingException exception) {
                errorMessage = " Couldn't parse mqtt brokerClusters from config file";
                log.error(EXCEPTION_CAUGHT + errorMessage + "-" + exception.getMessage(), exception);
                throw new Exception(errorMessage);
            }

        }
    }

        @Override
        public void initializeConnections()throws Exception {
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
