package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.SuccessResponseMessage;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.DatasetSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices.MockSensorPrototypeService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices.MockSensorService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.HttpSensorMessageRequestService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.KafkaSensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.MqttSensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SensorProtototypeServices.ProduceMockSensorDataService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SensorProtototypeServices.ReplaySensorCSVDataSetService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.ApplicationPropertiesUtil;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.Utils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.REQUEST_URI;

@Service
public class WorkloadGeneratorService implements IWorkloadGeneratorService{

    @Autowired
    public WorkloadGenerator workloadGenerator;

    @Autowired
    public MockSensorService mockSensorService;

    @Autowired
    public MockSensorPrototypeService mockSensorPrototypeService;

    @Autowired
    public DatasetSensorPrototypeService datasetSensorPrototypeService;

    @Autowired
    public HttpSensorMessageRequestService httpSensorMessageRequestService;

    @Autowired
    public MqttSensorMessageSendService mqttSensorMessageSendService;

    @Autowired
    public ApplicationPropertiesService applicationPropertiesService;

    private ISensorMessageSendService sensorMessageSendService;

    private List<ISensorDataProducerService> sensorDataProducerServices;

    private static final Logger log= LoggerFactory.getLogger(WorkloadGeneratorService.class);

    private ObjectMapper mapper=new ObjectMapper();

    public List<ISensorDataProducerService> getSensorDataProducerServices() {
        return sensorDataProducerServices;
    }

    public void setSensorDataProducerServices(List<ISensorDataProducerService> sensorDataProducerServices) {
        this.sensorDataProducerServices = sensorDataProducerServices;
    }

    @Override
    public synchronized void readConfigs(Exchange exchange) throws Exception{
        log.debug("Entered readConfigs");
        String errorMessage;
        try {
            String configsDirectory = ApplicationPropertiesUtil.getConfigsDirectory();
             String json = IOUtils.toString(new FileInputStream(configsDirectory + CONFIG_FILE), StandardCharsets.UTF_8);
            if(json.startsWith(UTF8_BOM))
                json = json.substring(1);
            JSONObject fileToJson = new JSONObject(json);
            if(fileToJson.has("configs"))
                workloadGenerator.setConfigs(fileToJson.getJSONObject("configs"));
            else
            {
                errorMessage="Could not load configs file";
                throw new Exception(errorMessage);
            }
            log.debug("Read configs");
        } catch (IOException e) {
            log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            errorMessage="Config file '"+CONFIG_FILE +"' could not be read";
            throw new Exception(errorMessage);
        }
        catch (JSONException e){
            log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            errorMessage="Couldn't parse config file";
            throw new Exception(errorMessage);
        }
        catch (Exception e){
            log.error("Exception caught: "+e.getMessage(),e);
            throw e;
        }
    }


    @Override
    public synchronized void initializeReceiverServiceConfiguration(Exchange exchange) throws Exception{
        log.debug("Entered initializeReceiverServiceConfiguration");
        String errorMessage= null;

        if(workloadGenerator.getConfigs().has(PROTOCOL)) {
            HashMap<String, String > protocolConfigs = new HashMap<>();
            if (workloadGenerator.getConfigs().get(PROTOCOL).equals(HTTP)) {
                if (workloadGenerator.getConfigs().has(HTTP_CONFIGS)) {
                    JSONObject httpConfigs = workloadGenerator.getConfigs().getJSONObject(HTTP_CONFIGS);
                    String requestURI;
                    String requestMethod;
                    if (httpConfigs.has(REQUEST_URI)) {
                        requestURI = httpConfigs.get(REQUEST_URI).toString();
                    } else{
                        errorMessage = "No requestURI was found in httpConfigs";
                        throw new ValidationException(errorMessage);}
//                    if (httpConfigs.has(REQUEST_METHOD)) {
//                        requestMethod = httpConfigs.get(FrameworkConstants.REQUEST_URI).toString();
//                    } else{
//                        errorMessage = "No requestMethod was found in httpConfigs";
//                        throw new ValidationException(errorMessage);}
                    protocolConfigs.put(REQUEST_URI , requestURI);
                    // protocolConfigs.put(REQUEST_METHOD, requestMethod);

                    if (httpConfigs.has(HTTP_SERVERS)) {
                        List<Server> httpServers;
                        try {
                            JSONArray httpServersJsonArray = httpConfigs.getJSONArray(HTTP_SERVERS);
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            httpServers = mapper.readValue(httpServersJsonArray.toString(), new TypeReference<List<Server>>() {
                            });
                            if (httpServers == null || httpServers.isEmpty()) {
                                errorMessage = " httpServers could not be created.Check logs or verify that file has valid httpServers";
                                throw new ValidationException(errorMessage);
                            }
                        } catch (JSONException | JsonMappingException exception) {
                            errorMessage = " Couldn't parse httpServers from config file";
                            log.error(EXCEPTION_CAUGHT + errorMessage + "-" + exception.getMessage(), exception);
                            throw new ValidationException(errorMessage);
                        } catch (Exception exception) {
                            if (exception instanceof ValidationException)
                                throw exception;
                            errorMessage = " Couldn't parse httpServers from config file" + exception.getMessage();
                            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
                            throw new Exception(errorMessage);
                        }
                        httpSensorMessageRequestService.initializeServiceReceiverConfigurations(httpServers, protocolConfigs);
                        sensorMessageSendService = httpSensorMessageRequestService;
                    } else {
                        errorMessage = " No httpServers configs was provided for protocol HTTP";
                        throw new Exception(errorMessage);
                    }
                } else {
                    errorMessage = " No httpConfigs was provided for protocol HTTP";
                    throw new Exception(errorMessage);
                }
            }
            else  if (workloadGenerator.getConfigs().get(PROTOCOL).equals(MQTT)) {
                if (workloadGenerator.getConfigs().has(MQTT_CONFIGS)) {
                    JSONObject mqttConfigs = workloadGenerator.getConfigs().getJSONObject(MQTT_CONFIGS);
                    String topic = null;
                    if (mqttConfigs.has(TOPIC)) {
                        topic = mqttConfigs.get(TOPIC).toString();
                        protocolConfigs.put(TOPIC_NAME, topic);
                    } else
                        log.warn("No root topic was provided. Default root topic is none");
                    if (mqttConfigs.has(MQTT_BROKER_CLUSTERS)) {
                        List<Server> mqttBrokerServers;
                        try {
                            JSONArray mqttBrokersJsonArray = mqttConfigs.getJSONArray(MQTT_BROKER_CLUSTERS);
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            mqttBrokerServers = mapper.readValue(mqttBrokersJsonArray.toString(), new TypeReference<List<Server>>() {
                            });
                            if (mqttBrokerServers == null || mqttBrokerServers.isEmpty()) {
                                errorMessage = " mqttBrokerServers could not be created.Check logs or verify that file has valid mqttBrokerServers";
                                throw new ValidationException(errorMessage);
                            }
                        } catch (JSONException | JsonMappingException exception) {
                            errorMessage = " Couldn't parse mqtt brokerClusters from config file";
                            log.error(EXCEPTION_CAUGHT + errorMessage + "-" + exception.getMessage(), exception);
                            throw new ValidationException(errorMessage);
                        } catch (Exception exception) {
                            if (exception instanceof ValidationException)
                                throw exception;
                            errorMessage = " Couldn't parse mqttBrokerClusters from config file" + exception.getMessage();
                            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
                            throw new Exception(errorMessage);
                        }
                        try {

                            mqttSensorMessageSendService.initializeServiceReceiverConfigurations(mqttBrokerServers, protocolConfigs);
                        }catch (Exception exception){
                            log.error("Error while initializing MQTT Configurations due to:" + exception.getMessage());
                            throw new Exception("Error while initializing MQTT Configurations due to:" + exception.getMessage());
                        }
                        sensorMessageSendService = mqttSensorMessageSendService;
                    }
                    else {
                        errorMessage = " No brokerClusters configs was provided for protocol MQTT";
                        throw new Exception(errorMessage);
                    }
                }
                else{
                    errorMessage = " No mqttConfigs was provided for protocol MQTT";
                    throw new Exception(errorMessage);
                }
            }
            else  if (workloadGenerator.getConfigs().get(PROTOCOL).equals(KAFKA)) {
                if (workloadGenerator.getConfigs().has(KAFKA_CONFIGS)) {
                    JSONObject kafkaConfigs = workloadGenerator.getConfigs().getJSONObject(KAFKA_CONFIGS);
                    String topic = null;
                    if (kafkaConfigs.has(TOPIC)) {
                        topic = kafkaConfigs.get(TOPIC).toString();
                        protocolConfigs.put(TOPIC_NAME, topic);
                    } else
                        log.warn("No root topic was provided. Default root topic is none");
                    if (kafkaConfigs.has(KAFKA_BROKER_CLUSTERS)) {
                        List<Server> kafkaBrokers;
                        try {
                            JSONArray kafkaBrokersJsonArray = kafkaConfigs.getJSONArray(KAFKA_BROKER_CLUSTERS);
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            kafkaBrokers = mapper.readValue(kafkaBrokersJsonArray.toString(), new TypeReference<List<Server>>() {
                            });
                            if (kafkaBrokers == null || kafkaBrokers.isEmpty()) {
                                errorMessage = " kafkaBrokers could not be created.Check logs or verify that file has valid kafkaBrokersClusters";
                                throw new ValidationException(errorMessage);
                            }
                        } catch (JSONException | JsonMappingException exception) {
                            errorMessage = " Couldn't parse  kafkaBrokers from config file";
                            log.error(EXCEPTION_CAUGHT + errorMessage + "-" + exception.getMessage(), exception);
                            throw new ValidationException(errorMessage);
                        } catch (Exception exception) {
                            if (exception instanceof ValidationException)
                                throw exception;
                            errorMessage = " Couldn't parse kafkaBrokers from config file" + exception.getMessage();
                            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
                            throw new Exception(errorMessage);
                        }
                        KafkaSensorMessageSendService kafkaSensorMessageSendService = new KafkaSensorMessageSendService();
                        kafkaSensorMessageSendService.initializeServiceReceiverConfigurations(kafkaBrokers, protocolConfigs);
                        sensorMessageSendService = kafkaSensorMessageSendService;
                    }
                    else {
                        errorMessage = " No kafkaBrokers configs was provided for protocol KAFKA";
                        throw new Exception(errorMessage);
                    }
                }
                else{
                    errorMessage = " No kafkaConfigs was provided for protocol KAFKA";
                    throw new Exception(errorMessage);
                }
            }
            else {
                errorMessage = " Protocol "+workloadGenerator.getConfigs().get(PROTOCOL)+" is not provided.Please provide one of the supported protocols :{HTTP,MQTT}";
                throw new Exception(errorMessage);
            }
        }
        else{
            errorMessage = " Protocol type has not been provided in configs file. Please provide one of the supported protocols :{HTTP,MQTT,KAFKA}";
            throw new Exception(errorMessage);
        }
    }



    @Override
    public synchronized void readSensorDataConfigs(Exchange exchange) throws Exception{
        log.debug("Entered readSensorDataConfigs");
        String errorMessage;

        if(workloadGenerator.getConfigs().has(SENSOR_DATA_CONFIGS)) {
            JSONObject sensorDataConfigs = workloadGenerator.getConfigs().getJSONObject(SENSOR_DATA_CONFIGS);
            if(sensorDataConfigs.has(SENSOR_PROTOTYPES)) {
                JSONArray sensorPrototypesJSONArray = sensorDataConfigs.getJSONArray(SENSOR_PROTOTYPES);
                List<MockSensorPrototype> mockSensorPrototypes = new ArrayList<>();
                List<DatasetSensorPrototype> datasetSensorPrototypes = new ArrayList<>();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                for (int i=0; i < sensorPrototypesJSONArray.length(); ++i){
                    JSONObject jsonObject = sensorPrototypesJSONArray.getJSONObject(i);
                    if(jsonObject.has(MOCK_SENSOR_PROTOTYPE)){
                        MockSensorPrototype mockSensorPrototype = mapper.readValue(jsonObject.getJSONObject(MOCK_SENSOR_PROTOTYPE).toString(), new TypeReference<MockSensorPrototype>(){});
                        if(mockSensorPrototypes == null)
                            log.error("mockSensorPrototype could not be parsed");
                        else
                            mockSensorPrototypes.add(mockSensorPrototype);
                    }
                    else  if(jsonObject.has(DATASET_SENSOR_PROTOTYPE)){
                        DatasetSensorPrototype datasetSensorPrototype = mapper.readValue(jsonObject.getJSONObject(DATASET_SENSOR_PROTOTYPE).toString(), new TypeReference<DatasetSensorPrototype>(){});
                        if(datasetSensorPrototype == null)
                            log.error("dataSetSensorPrototype could not be parsed");
                        else
                            datasetSensorPrototypes.add(datasetSensorPrototype);
                    }
                    else {
                        log.error("SensorPrototype provided in sensorPrototypes is not one of the supported types:{MockSensorPrototype, DatasetSensorPrototype}");
                    }
                }
                workloadGenerator.setMockSensorPrototypes(mockSensorPrototypes);
                workloadGenerator.setDatasetSensorPrototypes(datasetSensorPrototypes);
            }
            else{
                errorMessage = " configs_file does not contain sensorPrototypes";
                log.error(errorMessage);
                Utils.buildException(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
            }
        }
        else{
            errorMessage = " sensorDataConfigs has not been provided in configs file.";
            throw new Exception(errorMessage);
        }
    }



    @Override
    public synchronized void start(Exchange exchange)throws Exception {
        log.debug("Entered start()");
        // MockSensorPrototypesProducerThread mockSensorPrototypesProducerThread = new MockSensorPrototypesProducerThread(workloadGenerator.getMockSensorPrototypes());
        if (isInitialized()) {
            for (ISensorDataProducerService sensorDataProducerService : sensorDataProducerServices) {
                try {
                    sensorDataProducerService.resume(exchange);
                } catch (Exception exception) {
                    log.error("sensorPrototypeService " + sensorDataProducerService.getClass() + "could not be resumed due to :" + exception.getMessage(), exception);
                }
            }
        }
        else{
            sensorDataProducerServices = new ArrayList<>();
            if (!workloadGenerator.getMockSensorPrototypes().isEmpty())
                sensorDataProducerServices.add(new ProduceMockSensorDataService(workloadGenerator, mockSensorService, mockSensorPrototypeService, workloadGenerator.getMockSensorPrototypes()));
            if (!workloadGenerator.getDatasetSensorPrototypes().isEmpty())
                sensorDataProducerServices.add(new ReplaySensorCSVDataSetService(workloadGenerator, datasetSensorPrototypeService, workloadGenerator.getDatasetSensorPrototypes()));
            for (ISensorDataProducerService sensorDataProducerService : sensorDataProducerServices) {
                try {
                    sensorDataProducerService.initiate(exchange, sensorMessageSendService);
                } catch (Exception exception) {
                    log.error("sensorPrototypeService " + sensorDataProducerService.getClass() + " could not be initiated due to :" + exception.getMessage(), exception);
                }
            }
        }
        boolean started = false;
        for(ISensorDataProducerService sensorDataProducerService:sensorDataProducerServices){
            if(sensorDataProducerService.isStartedProducing()){
                started = true;
                break;}
        }

        if(started) {
            if(!isInitialized())
                workloadGenerator.setInitialized(true);
            workloadGenerator.setStarted(true);
        }else {
            log.error("Workload Generator could not be started. Check logs for errors");
            Utils.buildException(exchange,"Workload Generator could not be started. Check logs for errors",UNEXPECTED_ERROR_OCCURRED);
        }
    }

    @Override
    public synchronized void stop(Exchange exchange)throws Exception {
        for(ISensorDataProducerService sensorDataProducerService:sensorDataProducerServices){
            try {
                sensorDataProducerService.pause(exchange);
                workloadGenerator.setStarted(false);
            }catch (Exception exception){
                log.error("sensorPrototypeService "+ sensorDataProducerService.getClass() +"could not be paused due to :"+exception.getMessage(),exception );
                exchange.setProperty(ERROR_MESSAGE_TYPE, UNEXPECTED_ERROR_OCCURRED);
                exchange.setProperty(ERROR_MESSAGE, "Workload Generator could not be stopped due to "+exception.getMessage());
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
            }
        }
        SuccessResponseMessage successResponseMessage = new SuccessResponseMessage("Workload Generator has been successfully stopped!");
        exchange.setBody(successResponseMessage);
        log.info("Generator Stopped");

    }

    @Override
    public synchronized void restart(Exchange exchange)throws Exception {
        if (isStarted()) {
            exchange.setProperty(ERROR_MESSAGE_TYPE, UNEXPECTED_ERROR_OCCURRED);
            exchange.setProperty(ERROR_MESSAGE, "Workload Generator is already started! For restarting it, please stop it first");
            exchange.setHttpStatus(HTTP_BAD_REQUEST);
            return;
        } else if (!isInitialized()) {
            exchange.setProperty(ERROR_MESSAGE_TYPE, UNEXPECTED_ERROR_OCCURRED);
            exchange.setProperty(ERROR_MESSAGE, "Workload Generator could not be restarted because it is not initialized. Please start it first.");
            exchange.setHttpStatus(HTTP_BAD_REQUEST);
            return;
        } else {
            for(ISensorDataProducerService sensorDataProducerService : sensorDataProducerServices){
                try {
                    sensorDataProducerService.terminate(exchange);
                }catch (Exception exception){
                    log.error("sensorPrototypeService "+ mockSensorPrototypeService.getClass() +" could not be terminated due to :"+exception.getMessage(),exception );
                }
            }
            sensorMessageSendService.terminate();
            workloadGenerator.setStarted(false);
            workloadGenerator.setInitialized(false);
            try {
                readConfigs(exchange);
                initializeReceiverServiceConfiguration(exchange);
                readSensorDataConfigs(exchange);
                start(exchange);
                // started2=true;
            } catch (Exception exception) {
                log.error(UNEXPECTED_EXCEPTION_CAUGHT + "while trying to start workloadGenerator" + exception.getMessage());
                throw exception;
            }
        }
    }


    public boolean isStarted(){
        return workloadGenerator.isStarted();
    }

    public boolean isInitialized(){
        return workloadGenerator.isInitialized();
    }



    public synchronized void setNotInitialized()throws Exception{
        workloadGenerator.setInitialized(false);
    }



}
