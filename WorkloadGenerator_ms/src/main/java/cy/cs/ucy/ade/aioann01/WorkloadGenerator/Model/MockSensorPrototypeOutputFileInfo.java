package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.OutputFileEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;

import java.io.FileWriter;

public class MockSensorPrototypeOutputFileInfo {

    private  String filename;

    private String messageFieldsSeperator;

    private OutputFileEnum outputFileEnum;

    private FileWriter fileWriter;

    private String[] sensorMessageFieldNames;

    private SensorMessageEnum sensorMessageEnum;

    public SensorMessageEnum getSensorMessageEnum() {
        return sensorMessageEnum;
    }

    public void setSensorMessageEnum(SensorMessageEnum sensorMessageEnum) {
        this.sensorMessageEnum = sensorMessageEnum;
    }

    public String[] getSensorMessageFieldNames() {
        return sensorMessageFieldNames;
    }

    public void setSensorMessageFieldNames(String[] sensorMessageFieldNames) {
        this.sensorMessageFieldNames = sensorMessageFieldNames;
    }

    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessageFieldsSeperator() {
        return messageFieldsSeperator;
    }

    public void setMessageFieldsSeperator(String messageFieldsSeperator) {
        this.messageFieldsSeperator = messageFieldsSeperator;
    }

    public OutputFileEnum getOutputFileEnum() {
        return outputFileEnum;
    }

    public void setOutputFileEnum(OutputFileEnum outputFileEnum) {
        this.outputFileEnum = outputFileEnum;
    }
}
