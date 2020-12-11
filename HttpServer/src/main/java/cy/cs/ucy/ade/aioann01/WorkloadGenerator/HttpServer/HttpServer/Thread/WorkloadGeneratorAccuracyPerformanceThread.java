package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.Thread;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.Utils.Utils.*;

@Component
public class WorkloadGeneratorAccuracyPerformanceThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(WorkloadGeneratorAccuracyPerformanceThread.class);

    private Integer sensorMessageIntervalTime;

    public static int MILISECONDS_TO_SECONDS = 1000;

    private  String sensorPrototypeNameForEvaluation;

    private Map<String, SensorMetrics> sensorsMetricsMap;

    private BlockingDeque<SensorMessageDetails> blockingDeque;

    public WorkloadGeneratorAccuracyPerformanceThread() {
        this.blockingDeque = new LinkedBlockingDeque<SensorMessageDetails>();
        this.sensorsMetricsMap = new HashMap<>();
    }

    public void reset(){
        this.blockingDeque.clear();
        this.setSensorMessageIntervalTime(0);
        this.sensorsMetricsMap.clear();
    }

    public BlockingDeque<SensorMessageDetails> getBlockingDeque() {
        return blockingDeque;
    }

    public void setBlockingDeque(BlockingDeque<SensorMessageDetails> blockingDeque) {
        this.blockingDeque = blockingDeque;
    }

    public Integer getSensorMessageIntervalTime() {
        return sensorMessageIntervalTime;
    }

    public void setSensorMessageIntervalTime(Integer sensorMessageIntervalTime) {
        this.sensorMessageIntervalTime = sensorMessageIntervalTime;
    }

    public String getSensorPrototypeNameForEvaluation() {
        return sensorPrototypeNameForEvaluation;
    }

    public void setSensorPrototypeNameForEvaluation(String sensorPrototypeNameForEvaluation) {
        this.sensorPrototypeNameForEvaluation = sensorPrototypeNameForEvaluation;
    }


    public Map<String, SensorMetrics> getSensorsMetricsMap() {
        return sensorsMetricsMap;
    }

    public void setSensorsMetricsMap(Map<String, SensorMetrics> sensorsMetricsMap) {
        this.sensorsMetricsMap = sensorsMetricsMap;
    }

    public SensorMessageDetailsProcessed processMessage(SensorMessageDetails sensorMessageDetails ){
        SensorMessageDetailsProcessed sensorMessageDetailsProcessed = new  SensorMessageDetailsProcessed();
        String sensorId = null;
        Date messageDate;
        switch (sensorMessageDetails.getContentType()){

            case MediaType.APPLICATION_XML_VALUE:
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                try{
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document xmlDocument = documentBuilder.parse(new InputSource(new StringReader(sensorMessageDetails.getMessage())));
                    xmlDocument.getDocumentElement().normalize();

                    Node rootNode = xmlDocument.getFirstChild();
                    NodeList childNodes = rootNode.getChildNodes();
                    for(int i=0; i < childNodes.getLength(); ++i){
                        Node node = childNodes.item(i);
                        if(node != null && node.getNodeName().equals(SENSOR_ID_XML_ELEMENT)) {
                            sensorId = node.getTextContent();
                            break;}
                    }
                }catch (Exception e){
                    log.error("Could not parse XML payload");
                }
                break;

            case MediaType.APPLICATION_JSON_VALUE:
                try {
                    JSONObject requestPayload = new JSONObject(sensorMessageDetails.getMessage());
                    sensorId = requestPayload.getString("sensorId");
                } catch (JSONException e) {
                    log.error("Could not parse JSON payload");
                }
                break;

            case MediaType.TEXT_PLAIN_VALUE:
            default:
                break;

        }
        messageDate = new Date(sensorMessageDetails.getMessageDate());
        sensorMessageDetailsProcessed.setDate(messageDate);
        sensorMessageDetailsProcessed.setSensorId(sensorId);
        return sensorMessageDetailsProcessed;
    }


    @Override
    public void run() {

        log.debug("Evaluation Thread will start executing");
        try {
            while (true) {
                if (!blockingDeque.isEmpty()) {
                    SensorMessageDetails sensorMessageDetails = null;
                    try {
                        sensorMessageDetails = blockingDeque.take();
                        SensorMessageDetailsProcessed processedMessage = processMessage(sensorMessageDetails);
                        String messageSensorId = processedMessage.getSensorId();
                        String messageSensorIdInfo[] = messageSensorId.split("_");
                        if(messageSensorIdInfo.length == 3){
                            String sensorPrototype = messageSensorIdInfo[0];
                            if(sensorPrototype.equals(sensorPrototypeNameForEvaluation)){
                                String sensorId = messageSensorIdInfo[2];
                                SensorMetrics sensorMetrics = sensorsMetricsMap.get(sensorId);
                                if(sensorMetrics != null){
                                    Date previousDate = sensorMetrics.getPreviousSensorMessageDate();
                                    int expectedMaxMessageELapseTime = 0;

                                        expectedMaxMessageELapseTime =  sensorMessageIntervalTime;

//                                    else if (sensorPrototypeExpectedGenerationRate instanceof RandomGenerationRate){
//                                        expectedMaxMessageELapseTime =  (Integer)((RandomGenerationRate) sensorPrototypeExpectedGenerationRate).getMaxValue();
//                                    }
                                    int elapsedSensorMessagesTime = (int) ((processedMessage.getDate().getTime() - previousDate.getTime() )/MILISECONDS_TO_SECONDS);
                                    log.trace("For sensorId {"+sensorId+"} previous Date:"+previousDate +" currentDate:"+processedMessage.getDate()+ "elapsedSensorMessagesTime: "+elapsedSensorMessagesTime);
                                    sensorMetrics.setSensorsMessagesCount(sensorMetrics.getSensorsMessagesCount() + 1);
                                    if( elapsedSensorMessagesTime > expectedMaxMessageELapseTime){
                                        sensorMetrics.setSensorsMessagesDelaySum(sensorMetrics.getSensorsMessagesDelaySum() + (elapsedSensorMessagesTime - expectedMaxMessageELapseTime ));
                                    }

                                }
                                else{
                                    sensorMetrics = new SensorMetrics();
                                }
                                sensorMetrics.setPreviousSensorMessageDate(processedMessage.getDate());
                                sensorsMetricsMap.put(sensorId, sensorMetrics);
                            }
                        }

                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
