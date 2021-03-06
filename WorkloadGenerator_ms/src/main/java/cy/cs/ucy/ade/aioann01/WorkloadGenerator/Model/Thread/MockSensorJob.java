package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FileRecord;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.MockSensor;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.Scenario;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.ScenarioFieldValueInfo;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorFieldStatistics;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface.ISensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.ApplicationPropertiesUtil;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.UNEXPECTED_ERROR_OCCURRED;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.ASYNC_MESSAGES;


public class MockSensorJob extends Thread{

    private MockSensor mockSensor;

    private String sensorId;

    private WriterThread writerThread;

    private ISensorMessageSendService sensorMessageSendService;

    private volatile boolean stop;

    private volatile boolean pause;

    private SimpleDateFormat outputFileSimpleDateFormatter;

    private static final Logger log = LoggerFactory.getLogger(MockSensor.class);

    private boolean writeTofFile;

    private SensorFieldStatistics[] sensorFieldStatistics;

    private Map<String, ScenarioFieldValueInfo> scenarioFieldValueInfoMap;

    private Boolean scenarioMode;

    private Map<String, Object> propertiesMap;

    public MockSensorJob(MockSensor mockSensor) {
        propertiesMap = new HashMap<>();
        try {
            Boolean async = Boolean.valueOf(ApplicationPropertiesUtil.readPropertyFromConfigs(ASYNC_MESSAGES));
            propertiesMap.put(ASYNC_MESSAGES, async);
        }catch (Exception e){
            log.warn(ASYNC_MESSAGES + "was not provided. Default messages send type will be synchronous");
            propertiesMap.put(ASYNC_MESSAGES, false);
        }
        this.stop = false;
        this.pause = false;
        this.scenarioMode = false;
        this.sensorFieldStatistics = new SensorFieldStatistics[mockSensor.getMockSensorPrototype().getMessagePrototype().getFieldsPrototypes().size()];
        outputFileSimpleDateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for(int i=0; i<this.sensorFieldStatistics.length; ++i){
            sensorFieldStatistics[i] = SensorUtils.createSensorFieldValueStatistics(mockSensor.getMockSensorPrototype().getMessagePrototype().getFieldsPrototypes().get(i));
        }
        if(mockSensor.getMockSensorPrototype().getEvaluateFieldGenerationRate() == null)
            mockSensor.getMockSensorPrototype().setEvaluateFieldGenerationRate(false);
        this.mockSensor = mockSensor;
        this.sensorId = mockSensor.getId();
        this.setName(sensorId);
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public SensorFieldStatistics[] getSensorFieldStatistics() {
        return sensorFieldStatistics;
    }

    public void setSensorFieldStatistics(SensorFieldStatistics[] sensorFieldStatistics) {
        this.sensorFieldStatistics = sensorFieldStatistics;
    }

    public MockSensor getMockSensor() {
        return mockSensor;
    }

    public void setMockSensor(MockSensor mockSensor) {
        this.mockSensor = mockSensor;
    }

    public WriterThread getWriterThread() {
        return writerThread;
    }

    public void setWriterThread(WriterThread writerThread) {
        this.writerThread = writerThread;
    }

    public boolean isWriteTofFile() {
        return writeTofFile;
    }

    public void setWriteTofFile(boolean writeTofFile) {
        this.writeTofFile = writeTofFile;
    }

    public ISensorMessageSendService getSensorMessageSendService() {
        return sensorMessageSendService;
    }

    public void setSensorMessageSendService(ISensorMessageSendService sensorMessageSendService) {
        this.sensorMessageSendService = sensorMessageSendService;
    }

    public synchronized void triggerScenario(Scenario scenario){
        scenarioMode = true;
        scenarioFieldValueInfoMap = new HashMap<>();
        scenario.getScenarioFieldValueInfoList().stream()
                .forEach(scenarioFieldValueInfo -> scenarioFieldValueInfoMap.put(scenarioFieldValueInfo.getSensorFieldScenarioName(),scenarioFieldValueInfo));
    }

    public synchronized void terminateScenario(){
        scenarioMode = false;
    }

    public synchronized void terminate() throws IOException {
        this.stop = true;
        if(this.pause == true)
            this.notify();
    }

    public synchronized  void pause() throws IOException,InterruptedException {
        this.pause = true;
    }

    public synchronized void resume2() throws InterruptedException, IOException {
        this.notify();
    }
    
    public void sendMessage()throws RuntimeException {
        String message = SensorUtils.createMessage(mockSensor.getMockSensorPrototype().getMessagePrototype(),
                this.sensorFieldStatistics,
                this.mockSensor.getMockSensorPrototype().getEvaluateFieldGenerationRate(),
                scenarioMode,
                scenarioFieldValueInfoMap);
        if (writeTofFile) {
            logToFile(message);
        }
        SensorMessageEnum contentType = getMockSensor().getMockSensorPrototype().getMessagePrototype().getSensorMessageType();
        String sensorId = getMockSensor().getId();
        Boolean async = (Boolean) (propertiesMap.get(ASYNC_MESSAGES));
        //log.trace("Sending message for sensorId {"+sensorId+"}. Message: "+message);
        if (async) {
            CompletableFuture.runAsync(() -> {
                try {
                    sensorMessageSendService.sendMessage(sensorId, message, contentType);
                } catch (Exception exception) {
                    log.error("Error while sending sensor message: " + exception.getMessage(), exception);
                }
            });
        } else {
            try {
                sensorMessageSendService.sendMessage(sensorId, message, contentType);
            } catch (Exception exception) {
                log.error("Error while sending sensor message: " + exception.getMessage(), exception);
            }
        }
    }


    public void logToFile(String message){
        Date date;
        date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        FileRecord newRecord = new FileRecord();
        newRecord.setDate(outputFileSimpleDateFormatter.format(date));
        newRecord.setTimestamp(ts.toString());
        newRecord.setSensorId(mockSensor.getId());
        newRecord.setMessage(message);
        newRecord.setMockSensorPrototypeName(mockSensor.getMockSensorPrototype().getSensorPrototypeName());
        writerThread.getBlockingDeque().add(newRecord);
    }

    @Override
    public void run() {
        log.debug("SensorJob {" + this.sensorId + "} started");
        try{
            while (!stop) {
                if(pause){
                    synchronized (this){
                        log.debug("SensorJob {" + this.sensorId + "} will pause");
                        wait();
                        pause = false;
                        if(stop == true)
                            break;
                        log.debug("SensorJob {" + this.sensorId + "} will woke up and will resume");
                    }
                }
                try {//Send Message
                    sendMessage();
                } catch (Throwable throwable) {
                    log.error(UNEXPECTED_ERROR_OCCURRED + throwable.getMessage(), throwable);
                }
                try {//Calculate sleep time and sleep
                    Integer waitTime = 0;
                    waitTime = (Integer)SensorUtils.generateGenerationRateValue(this.mockSensor.getMockSensorPrototype().getGenerationRate(), TypesEnum.INTEGER);
                    if(waitTime < 0)
                        waitTime = 0;
                    Thread.sleep(waitTime * 1000);//seconds to ms
                    //log.trace("sensorId {" + mockSensor.getId() + "} WaitTime was:"+waitTime);
                }catch (Exception e){
                    log.error("Could not calculate generation rate for sensor:" + mockSensor.getMockSensorPrototype().getSensorPrototypeName(), e);
                }
            }
            log.debug("SensorJob {" + this.sensorId + "} has been successfully terminated");

        }catch (Throwable throwable){
            log.error(UNEXPECTED_ERROR_OCCURRED +" in inside SensorJob {"  + this.sensorId+ "} run()" + throwable.getMessage());
            throw new RuntimeException(throwable.getMessage());
        }}

}
