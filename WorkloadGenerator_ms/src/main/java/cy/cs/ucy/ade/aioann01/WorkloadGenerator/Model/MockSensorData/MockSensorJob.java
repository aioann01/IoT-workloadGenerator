package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FileRecord;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorFieldStatistics;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.WriterThread;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.KafkaSensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.MqttSensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.GenerationRatesUtils;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.UNEXPECTED_ERROR_OCCURRED;


public class MockSensorJob implements Runnable{
    private MockSensor mockSensor;

    private WriterThread writerThread;

    private ISensorMessageSendService sensorMessageSendService;

    private static final Logger log = LoggerFactory.getLogger(MockSensor.class);

    private boolean writeTofFile;

    private SensorFieldStatistics[] sensorFieldStatistics;

    public MockSensorJob(MockSensor mockSensor) {
        this.sensorFieldStatistics = new SensorFieldStatistics[mockSensor.getMockSensorPrototype().getMessagePrototype().getFieldsPrototypes().size()];
        for(int i=0; i<this.sensorFieldStatistics.length; ++i){
            sensorFieldStatistics[i] = SensorUtils.createSensorFieldValueStatistics(mockSensor.getMockSensorPrototype().getMessagePrototype().getFieldsPrototypes().get(i));
        }
        if(mockSensor.getMockSensorPrototype().getEvaluateFieldGenerationRate() == null)
            mockSensor.getMockSensorPrototype().setEvaluateFieldGenerationRate(false);
        this.mockSensor = mockSensor;
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

    public void sendMessage()throws RuntimeException{
        String message = SensorUtils.createMessage(mockSensor.getMockSensorPrototype().getMessagePrototype(), this.sensorFieldStatistics, this.mockSensor.getMockSensorPrototype().getEvaluateFieldGenerationRate());


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date;
        try {
            date = new Date();
            int waitTime = (Integer)GenerationRatesUtils.generateGenerationRateValue(this.mockSensor.getMockSensorPrototype().getGenerationRate(), TypesEnum.INTEGER, this.mockSensor.getMockSensorPrototype().getGenerationRateType());

            // Random rand = new Random();
            // ConstantGenerationRate l = (ConstantGenerationRate) mockSensor.getMockSensorPrototype().getGenerationRate();
            //int rnd = rand.nextInt((Integer) l.getValue());
            //System.out.println(formatter.format(date)+" ThreadId:["+Thread.currentThread().getId()+"] |ThreadName:["+Thread.currentThread().getName()+"] Sensor with id: "+id+" will wait for "+rnd);
            //Thread.sleep(1000*rnd);//TimeUnit.SECONDS.sleep(rnd);
            //   Thread.sleep((Integer) l.getValue()*1000);//seconds to ms
            //log.debug("WAIT TIME FOR {"+ this.mockSensor.getId()+"} is:" + waitTime);
            Thread.sleep(waitTime * 1000);//seconds to ms

        }catch (Exception e){
            log.error("Could not calculate generation rate for sensor");
        }
        date = new Date();
        Timestamp ts = new Timestamp(date.getTime());

//        System.out.println(formatter.format(date)+" ThreadId:["+Thread.currentThread().getId()+"] |ThreadName:["+Thread.currentThread().getName()+"] Sensor with id: "+id+" says hello!");
        if(writeTofFile) {
            FileRecord newRecord = new FileRecord();
            newRecord.setDate(formatter.format(date));
            newRecord.setTimestamp(ts.toString());
            newRecord.setSensorId(mockSensor.getId());
            newRecord.setMessage(message
                    /*"ThreadId:["+Thread.currentThread().getId()+"] |ThreadName:["+Thread.currentThread().getName()+"] Sensor with id: "+id+" says hello!"*/);
            newRecord.setMockSensorPrototypeName(mockSensor.getMockSensorPrototype().getSensorPrototypeName());
            writerThread.getBlockingDeque().add(newRecord);
        }
        try {
            SensorMessageEnum contentType = getMockSensor().getMockSensorPrototype().getMessagePrototype().getSensorMessageType();
            String sensorId = getMockSensor().getId();
           // log.trace("Sending message: "+message+" for sensorId {"+mockSensor.getId()+"}");
            if(sensorMessageSendService instanceof MqttSensorMessageSendService)
                sensorMessageSendService.sendMessage(getMockSensor().getMockSensorPrototype().getSensorPrototypeName()+"/"+ mockSensor.getId(),message, contentType);
            else if(sensorMessageSendService instanceof KafkaSensorMessageSendService){
                sensorMessageSendService.sendMessage(getMockSensor().getMockSensorPrototype().getSensorPrototypeName(), message, contentType);
            }
            else
                sensorMessageSendService.sendMessage(sensorId, message, contentType);
        }catch (Exception exception){
            log.error("Error while sending sensor message: "+exception.getMessage(),exception);
        }

    }

    @Override
    public void run() {
        try{
      /*  System.out.println();
        System.out.println("Started ThreadId:["+Thread.currentThread().getId()+"] |ThreadName:["+Thread.currentThread().getName()+"] for "+sensorPrototype.getName());*/

            sendMessage();
       /* System.out.println("Finished ThreadId:["+Thread.currentThread().getId()+"] |ThreadName:["+Thread.currentThread().getName()+"] for "+sensorPrototype.getName());
        System.out.println();*/
        }catch (Throwable throwable){
            log.error(UNEXPECTED_ERROR_OCCURRED+throwable.getMessage(),throwable);
            //throw new RuntimeException(throwable.getMessage());
        }}
}
