package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.OutputFileEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FileRecord;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorPrototypeOutputFileInfo;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.OutputFileEnum.CSV;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.OutputFileEnum.TXT;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;

public class WriterThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(WriterThread.class);

    private BlockingDeque<FileRecord> blockingDeque;

    private HashMap<String, MockSensorPrototypeOutputFileInfo>  mockSensorPrototypesOutputFileInfoMap;

    private volatile boolean stop;

    private volatile boolean pause;

    private Throwable throwableCaught;

    public WriterThread() {
        this.stop = false;
        this.mockSensorPrototypesOutputFileInfoMap = new HashMap<String,MockSensorPrototypeOutputFileInfo>();
        this.blockingDeque = new LinkedBlockingDeque<FileRecord>();

    }

    public void addMockSensorPrototypeOutputFileInfo(String mockSensorPrototypeName , String mockSensorPrototypeOutputFilename) throws IOException,Exception {
        String fileTokens[] = mockSensorPrototypeOutputFilename.split("\\.");
        MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = new MockSensorPrototypeOutputFileInfo();
        if(fileTokens[1]==null)
            throw new Exception("No extension has been provided for output file. Provide one of {.csv, .txt}");
        else{
            if (fileTokens[1].equals(TEXT_EXTENSION)) {
                mockSensorPrototypeOutputFileInfo.setOutputFileEnum(TXT);
                mockSensorPrototypeOutputFileInfo.setMessageFieldsSeperator(TEXT_COLUMN_SEPERATOR);
            } else if (fileTokens[1].equals(EXCEL_FILENAME)) {
                mockSensorPrototypeOutputFileInfo.setOutputFileEnum(CSV);
                mockSensorPrototypeOutputFileInfo.setMessageFieldsSeperator(EXCEL_COLUMN_SEPERATOR);
            } else {
                throw new Exception("Unsupported output file format. Supported file formats are : {.csv, .txt}");
            }
        }
        mockSensorPrototypeOutputFileInfo.setFilename(mockSensorPrototypeOutputFilename);
        mockSensorPrototypeOutputFileInfo.setFileWriter(new FileWriter(mockSensorPrototypeOutputFilename));
        this.mockSensorPrototypesOutputFileInfoMap.put(mockSensorPrototypeName,mockSensorPrototypeOutputFileInfo);
    }
    public synchronized void  terminate() throws  IOException {

        Iterator it = mockSensorPrototypesOutputFileInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry mockSensorPrototypesOutputFileInfoEntry = (Map.Entry)it.next();
            MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = (MockSensorPrototypeOutputFileInfo)(mockSensorPrototypesOutputFileInfoEntry.getValue());
            try {
                mockSensorPrototypeOutputFileInfo.getFileWriter().close();
            }catch (IOException ioException){
                throw new IOException("file Writer on file "+mockSensorPrototypeOutputFileInfo.getFilename() +" of WriterThread could not be closed due to "+ioException.getMessage() +" .Please verify that the file is not open");
            }}
        this.stop=true;
    }


    public synchronized  void pause() throws IOException,InterruptedException {
        Iterator it = mockSensorPrototypesOutputFileInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry mockSensorPrototypesOutputFileInfoEntry = (Map.Entry)it.next();
            MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = (MockSensorPrototypeOutputFileInfo)(mockSensorPrototypesOutputFileInfoEntry.getValue());
            try {
                mockSensorPrototypeOutputFileInfo.getFileWriter().close();
            }catch (IOException ioException){
                throw new IOException("file Writer on file "+mockSensorPrototypeOutputFileInfo.getFilename() +" of WriterThread could not be closed due to "+ioException.getMessage() +" .Please verify that the file is not open");
            }}
        this.pause = true;

    }

    public synchronized  void resume2() throws InterruptedException, IOException {
        this.notify();
        Iterator it = mockSensorPrototypesOutputFileInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry mockSensorPrototypesOutputFileInfoEntry = (Map.Entry)it.next();
            MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = (MockSensorPrototypeOutputFileInfo)(mockSensorPrototypesOutputFileInfoEntry.getValue());
            try {
                mockSensorPrototypeOutputFileInfo.setFileWriter(new FileWriter(mockSensorPrototypeOutputFileInfo.getFilename(), true));
            }catch (IOException ioException){
                throw new IOException("file Writer on file "+mockSensorPrototypeOutputFileInfo.getFilename() +" of WriterThread could not be opened due to "+ioException.getMessage() +" .Please verify that the file is not open");
            }}

    }

    public BlockingDeque<FileRecord> getBlockingDeque() {
        return blockingDeque;
    }

    public void setBlockingDeque(BlockingDeque<FileRecord> blockingDeque) {
        this.blockingDeque = blockingDeque;
    }


    public void run() {
        log.debug("WriterThread will start executing");
        try{
            while (!stop) {
                if(pause){
                    synchronized (this){
                        log.debug("WriterThread will sleep");
                        wait();
                        pause = false;
                        log.debug("WriterThread wakes up and will continue tasks");
                    }
                }
                if (!blockingDeque.isEmpty()) {
                    FileRecord newRecord = null;
                    try {
                        newRecord = blockingDeque.take();
                    } catch (InterruptedException e) {
                        log.error(UNEXPECTED_ERROR_OCCURRED+e.getMessage(),e);
                        throw new RuntimeException(e.getMessage());
                    }
                    try {
                        MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = this.mockSensorPrototypesOutputFileInfoMap.get(newRecord.getMockSensorPrototypeName());
                        StringBuilder record = new StringBuilder();
                        record.append(newRecord.getDate()+mockSensorPrototypeOutputFileInfo.getMessageFieldsSeperator());
                        record.append(newRecord.getSensorId()+mockSensorPrototypeOutputFileInfo.getMessageFieldsSeperator());
                        if(mockSensorPrototypeOutputFileInfo.getOutputFileEnum().equals(CSV)){
                            try {
                                JSONObject messageJSONObject = new JSONObject(newRecord.getMessage().toString().trim());
                                Iterator<String> keys = messageJSONObject.keys();
                                while(keys.hasNext()) {
                                    String key = keys.next();
//                                if (messageJSONObject.get(key) instanceof JSONObject) {
//                                    record.append(messageJSONObject.get(key));
//                                }
//                                else
                                    record.append(messageJSONObject.get(key));
                                    if(keys.hasNext())
                                        record.append(mockSensorPrototypeOutputFileInfo.getMessageFieldsSeperator());
                                }
                            }catch (Exception exception){
                                record.append("\""+newRecord.getMessage()+"\"");//Message is not JSONObject. Will write the record in
                            }}
                        else
                            record.append("\""+newRecord.getMessage()+"\"");//For not CSV output file formats we print the message as it is created

                        //  fileWriter.write(newRecord.getDate()+" "+newRecord.getMessage()+"\n");
                        mockSensorPrototypeOutputFileInfo.getFileWriter().write(record.toString()+"\n");
                        mockSensorPrototypeOutputFileInfo.getFileWriter().flush();

                    } catch (IOException e) {
                        log.error(EXCEPTION_CAUGHT+"inside WriterThread run() - Could not create file: "+e.getMessage());
                        throw  new RuntimeException("Could not write sensor message to file due to:"+e.getMessage());
                    }
                }
            }
        }catch (Throwable throwable){
            throwableCaught = throwable;
            log.error(UNEXPECTED_ERROR_OCCURRED+" inside WriterThread run(): "+throwable.getMessage());
            throw new RuntimeException(throwable.getMessage());
        }
    }
}
