package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.OutputFileEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FieldPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FileRecord;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorPrototypeOutputFileInfo;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

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

    public void addMockSensorPrototypeOutputFileInfo(MockSensorPrototype mockSensorPrototype) throws IOException,Exception {
        String fileTokens[] = mockSensorPrototype.getOutputFile().split("\\.");
        MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = new MockSensorPrototypeOutputFileInfo();
        mockSensorPrototypeOutputFileInfo.setFilename(mockSensorPrototype.getOutputFile());
        mockSensorPrototypeOutputFileInfo.setFileWriter(new FileWriter(mockSensorPrototype.getOutputFile()));
        mockSensorPrototypeOutputFileInfo.setSensorMessageEnum(mockSensorPrototype.getMessagePrototype().getSensorMessageType());
        if(fileTokens[1] == null)
            throw new Exception("No extension has been provided for output file. Provide one of {.csv, .txt}");
        else{
            if (fileTokens[1].equals(TEXT_EXTENSION)) {
                mockSensorPrototypeOutputFileInfo.setOutputFileEnum(TXT);
                mockSensorPrototypeOutputFileInfo.setMessageFieldsSeperator(TEXT_COLUMN_SEPERATOR);
            } else if (fileTokens[1].equals(EXCEL_FILENAME)) {
                mockSensorPrototypeOutputFileInfo.setOutputFileEnum(CSV);
                mockSensorPrototypeOutputFileInfo.setMessageFieldsSeperator(EXCEL_COLUMN_SEPERATOR);
                mockSensorPrototypeOutputFileInfo.getFileWriter().write("Timestamp,SensorId,");
                List<String> sensorMessageFieldNames = mockSensorPrototype.getMessagePrototype().getFieldsPrototypes()
                        .stream()
                        .map(fieldPrototype -> fieldPrototype.getName())
                        .collect(Collectors.toList());
                mockSensorPrototypeOutputFileInfo.setSensorMessageFieldNames(sensorMessageFieldNames.toArray(new String[0]));
                mockSensorPrototypeOutputFileInfo.getFileWriter().write(String.join( ",", sensorMessageFieldNames)+ "\n");
                mockSensorPrototypeOutputFileInfo.getFileWriter().flush();
            } else {
                throw new Exception("Unsupported output file format. Supported file formats are : {.csv, .txt}");
            }
        }
        this.mockSensorPrototypesOutputFileInfoMap.put(mockSensorPrototype.getSensorPrototypeName(), mockSensorPrototypeOutputFileInfo);
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
                        log.error(UNEXPECTED_ERROR_OCCURRED + e.getMessage(), e);
                        throw new RuntimeException(e.getMessage());
                    }

                    MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo = this.mockSensorPrototypesOutputFileInfoMap.get(newRecord.getMockSensorPrototypeName());
                    String record = null;
                    try {
                        record = SensorUtils.createCSVOutputRecord(mockSensorPrototypeOutputFileInfo, newRecord);
                    } catch (Exception e) {

                    }

                    try{
                        //  fileWriter.write(newRecord.getDate()+" "+newRecord.getMessage()+"\n");
                        mockSensorPrototypeOutputFileInfo.getFileWriter().write(record+"\n");
                        mockSensorPrototypeOutputFileInfo.getFileWriter().flush();

                    } catch (IOException e) {
                        log.error(EXCEPTION_CAUGHT_WHILE + "inside WriterThread run() - Could not create file: "+e.getMessage());
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
