package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorFieldStatistics;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.DatasetSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.DatasetSensorPrototypeService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.EXCEL_COLUMN_SEPERATOR;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ReplayDatasetSensorPrototypeThread extends Thread{

    private static final Logger log= LoggerFactory.getLogger(ReplayDatasetSensorPrototypeThread.class);

    private DatasetSensorPrototype datasetSensorPrototype;

    private DatasetSensorPrototypeService datasetSensorPrototypeService;

    private Boolean datasetSensorPrototypeIsCorrectlySet;

    private BufferedReader bufferedReader;

    private SensorFieldStatistics[] datasetValuesStatistics;

    private volatile boolean stop;

    private volatile boolean pause;

    private volatile  boolean finished;

    private int recordsCount = 0;

    private int totalWaitTime = 0;

    SimpleDateFormat formatter = null;

    public SensorFieldStatistics[] getDatasetValuesStatistics() {
        return datasetValuesStatistics;
    }

    public void setDatasetValuesStatistics(SensorFieldStatistics[] datasetValuesStatistics) {
        this.datasetValuesStatistics = datasetValuesStatistics;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Boolean getDatasetSensorPrototypeIsCorrectlySet() {
        return datasetSensorPrototypeIsCorrectlySet;
    }

    public DatasetSensorPrototypeService getDatasetSensorPrototypeService() {
        return datasetSensorPrototypeService;
    }

    public void setDatasetSensorPrototypeService(DatasetSensorPrototypeService datasetSensorPrototypeService) {
        this.datasetSensorPrototypeService = datasetSensorPrototypeService;
    }

    public void setDatasetSensorPrototypeIsCorrectlySet(Boolean datasetSensorPrototypeIsCorrecltySet) {
        this.datasetSensorPrototypeIsCorrectlySet = datasetSensorPrototypeIsCorrecltySet;
    }

    public ReplayDatasetSensorPrototypeThread(DatasetSensorPrototypeService datasetSensorPrototypeService, DatasetSensorPrototype datasetSensorPrototype){
        this.datasetSensorPrototypeService = datasetSensorPrototypeService;
        this.stop = false;
        this.finished = false;
        this.pause = false;
        try {
            datasetSensorPrototypeService.createDatasetSensorPrototype(datasetSensorPrototype);
        }catch (Exception exception){
            datasetSensorPrototypeIsCorrectlySet = false;
            log.error(EXCEPTION_CAUGHT +" while creating datasetSensorPrototype :" +exception.getMessage(),exception);
            return;
        }
        File file = datasetSensorPrototypeService.validateFile(
                datasetSensorPrototype.isSortedDataset(),
                datasetSensorPrototype.getDatasetFile(),
                datasetSensorPrototype.getSensorIdColumnName(),
                datasetSensorPrototype.getTimestampedDataset(),
                datasetSensorPrototype.getTimestampColumnName(),
                datasetSensorPrototype.getSensorPrototypeName(),
                datasetSensorPrototype.getTimestampFormat());
        if(file == null){
            datasetSensorPrototypeIsCorrectlySet = false;
            log.error("Dataset file {"+datasetSensorPrototype.getDatasetFile()+"} is not valid for datasetSensorPrototype {"+datasetSensorPrototype.getSensorPrototypeName()+"}.DatasetSensorPrototype will be ignored.");
            return;
        }
        else{
            try {
                this.bufferedReader = new BufferedReader(new FileReader(file));
            }catch (Exception exception){
                log.error("Dataset file {"+datasetSensorPrototype.getDatasetFile()+"} could not be opened for  datasetSensorPrototype {"+datasetSensorPrototype.getSensorPrototypeName()+"} due to "+exception.getMessage()+".DatasetSensorPrototype will be ignored.",exception);
                datasetSensorPrototypeIsCorrectlySet = false;
                return;
            }
        }
        datasetSensorPrototypeIsCorrectlySet = true;
        this.datasetSensorPrototype = datasetSensorPrototype;


    }


    public synchronized void  terminate() throws IOException {
        try {
            this.bufferedReader.close();
        }catch (IOException ioException){
            throw new IOException("BufferReader for file "+datasetSensorPrototype.getDatasetFile() +" of WriterThread could not be closed due to "+ioException.getMessage() +" .Please verify that the file is not open");
        }
        this.stop=true;
    }

    public synchronized  void pause() throws IOException,InterruptedException {
        if(datasetSensorPrototype.getExportGenerationRate() !=null && datasetSensorPrototype.getExportGenerationRate()){
            SensorUtils.exportSensorPrototypes(datasetValuesStatistics, recordsCount, datasetSensorPrototype.getSensorPrototypeName(), totalWaitTime);
        }
        this.pause = true;
    }

    public synchronized  void resume2() throws InterruptedException, IOException {
        this.notify();
    }


    public void run() {
        log.debug("DatasetSensorPrototype {"+datasetSensorPrototype.getSensorPrototypeName()+"} Started");
        try {
            String sensorMessageFields = datasetSensorPrototype.getSensorMessageFields();

            long waitTime = 0;
            try {
                if(datasetSensorPrototype.getTimestampedDataset()){
                    //if(!datasetSensorPrototype.isSortedDataset())
//                        formatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT_FOR_SORTED_CSV);
//                    else
                        formatter = new SimpleDateFormat(datasetSensorPrototype.getTimestampFormat());

                }
            }catch (Exception exception){
                log.error(EXCEPTION_CAUGHT+" while trying to parse timestamp format {"+datasetSensorPrototype.getTimestampFormat()+"} for Dataset file {"+datasetSensorPrototype.getDatasetFile()+"}.Check that timestamp format is valid.");
                throw new Exception("Provided  timestamp format {"+datasetSensorPrototype.getTimestampFormat()+"} for Dataset file {"+datasetSensorPrototype.getDatasetFile()+"} is not valid");
            }
            Date previousDate = null;
            int sensorIDColumnIndex = -1;
            int timestampColumnIndex= 0;
            String firstLine = bufferedReader.readLine().replaceAll("\"","");
            String[] columnNames = firstLine.split(EXCEL_COLUMN_SEPERATOR);
            for(int i=0; i<columnNames.length;++i){
                if(columnNames[i].equals(datasetSensorPrototype.getSensorIdColumnName()))
                    sensorIDColumnIndex = i;
                if(datasetSensorPrototype.getTimestampedDataset() && columnNames[i].equals(datasetSensorPrototype.getTimestampColumnName()))
                    timestampColumnIndex = i;
            }
            String firstRecord = bufferedReader.readLine().replaceAll("\"","");
            String [] firstColumnValues = firstRecord.split(EXCEL_COLUMN_SEPERATOR);
            try {
                if (datasetSensorPrototype.getTimestampedDataset()) {
                    previousDate = formatter.parse(firstColumnValues[timestampColumnIndex]);
                }
            }catch (ParseException e){
                log.error(EXCEPTION_CAUGHT+" while trying to parse first record timestamp for Dataset file {"+datasetSensorPrototype.getDatasetFile()+"}.Check that timestamp is of format : {"+datasetSensorPrototype.getTimestampFormat()+"} and that format is valid date format",e);
                throw new Exception("First record timestamp is not of format : {"+datasetSensorPrototype.getTimestampFormat()+"} for Dataset file {"+datasetSensorPrototype.getDatasetFile()+"}");

            }
            String record = firstRecord;
            if(datasetSensorPrototype.getExportGenerationRate()){
                datasetValuesStatistics = new SensorFieldStatistics[firstColumnValues.length];
                for(int i=0;i<firstColumnValues.length;++i){
                    datasetValuesStatistics[i] = SensorUtils.createSensorFieldValueStatistics(firstColumnValues[i], columnNames[i]);
                }
            }

            while(record != null || !stop) {
                if (pause) {
                    synchronized (this) {
                        log.debug("ReplayDatasetSensorPrototypeThread for datasetSensorPrototype {" + datasetSensorPrototype.getSensorPrototypeName() + "} will sleep");
                        wait();
                        pause = false;
                        log.debug("ReplayDatasetSensorPrototypeThread for datasetSensorPrototype {" + datasetSensorPrototype.getSensorPrototypeName() + "}  wakes up and will continue tasks");
                    }
                }
                String[] columnValues = record.split(EXCEL_COLUMN_SEPERATOR);

                if (datasetSensorPrototype.getTimestampedDataset()) {
                    Date currentDate = formatter.parse(columnValues[timestampColumnIndex]);
                    if(currentDate == null){
                        log.error("Unexcpected error occured while trying to parse timestamp {"+columnValues[timestampColumnIndex]+"} for Dataset file {"+datasetSensorPrototype.getDatasetFile()+"}.Check that timestamp is of format : {"+datasetSensorPrototype.getTimestampFormat()+"} and that format is valid date format. Record will be ignored");
                        record = bufferedReader.readLine();
                        continue;
                    }
                    waitTime =  (currentDate.getTime()-previousDate.getTime())/MILISECONDS_TO_SECONDS;//-.(currentTs.getTime() - previousTimestamp.getTime()) / MILISECONDS_TO_SECONDS; //Calculate time in ms and convert to Seconds
                    previousDate = currentDate;
                } else
                    waitTime = datasetSensorPrototypeService.calculateNextMessageTimeForGenerationRate(datasetSensorPrototype.getGenerationRate(), datasetSensorPrototype.getGenerationRateType());
                if(waitTime >= 0){
                    SECONDS.sleep(waitTime);
                    String sensorId = datasetSensorPrototype.getSensorIdColumnName() == null || datasetSensorPrototype.getSensorIdColumnName().isEmpty()
                            ?datasetSensorPrototype.getSensorPrototypeName()
                            :columnValues[sensorIDColumnIndex];
                    String message = datasetSensorPrototypeService.createDatasetSensorPrototypeMessage(datasetSensorPrototype.getExportGenerationRate(), datasetValuesStatistics, datasetSensorPrototype.getSensorPrototypeName(), columnNames, columnValues, sensorIDColumnIndex, timestampColumnIndex, sensorMessageFields, datasetSensorPrototype.getMessagePrototype(), datasetSensorPrototype.getSensorMessageEnum());
                    this.recordsCount++;
                    this.totalWaitTime += waitTime;
                    if (message != null){
                            datasetSensorPrototypeService.sendMessage(sensorId, message, datasetSensorPrototype.getSensorMessageEnum());
                    }
                    log.trace("Sending message: "+message+" for sensorId {"+sensorId+"}");
                }
                else
                    log.error("Could not calculate next message time for {"+datasetSensorPrototype.getSensorPrototypeName()+"} .Confirm that timestamps are in time order ascending");
                log.debug("woke up after "+waitTime);
                record = bufferedReader.readLine().replaceAll("\"","");
            }
            log.info("DataSetSensorPrototype {"+datasetSensorPrototype+"} has finished");
            finished = true;
        }catch (Exception exception){
            log.error(EXCEPTION_CAUGHT + " while trying to read Dataset file {"+datasetSensorPrototype.getDatasetFile()+"} is not valid for datasetSensorPrototype {"+datasetSensorPrototype.getSensorPrototypeName()+"}.DatasetSensorPrototype will be ignored.",exception);
        }
    }
}
