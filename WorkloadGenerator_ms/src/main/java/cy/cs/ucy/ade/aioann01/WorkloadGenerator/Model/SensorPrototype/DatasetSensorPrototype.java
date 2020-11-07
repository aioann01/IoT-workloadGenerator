package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;

public class DatasetSensorPrototype extends SensorPrototype {

    private Boolean timestampedDataset;

    private String datasetFile;

    private String sensorMessageFields;

    private String sensorIdColumnName;

    private String timestampColumnName;

    private String messageExportType;

    private SensorMessageEnum sensorMessageEnum;

    private String timestampFormat;

    private Boolean sortedDataset;

    private Boolean exportGenerationRate;

    public Boolean getExportGenerationRate() {
        return exportGenerationRate;
    }

    public void setExportGenerationRate(Boolean exportGenerationRate) {
        this.exportGenerationRate = exportGenerationRate;
    }

    public Boolean isSortedDataset() {
        return sortedDataset;
    }

    public void setSortedDataset(Boolean sortedDataset) {
        this.sortedDataset = sortedDataset;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public SensorMessageEnum getSensorMessageEnum() {
        return sensorMessageEnum;
    }

    public void setSensorMessageEnum(SensorMessageEnum sensorMessageEnum) {
        this.sensorMessageEnum = sensorMessageEnum;
    }

    public String getMessageExportType() {
        return messageExportType;
    }

    public void setMessageExportType(String messageExportType) {
        this.messageExportType = messageExportType;
    }

    public Boolean getTimestampedDataset() {
        return timestampedDataset;
    }

    public void setTimestampedDataset(Boolean timestampedDataset) {
        this.timestampedDataset = timestampedDataset;
    }

    public String getDatasetFile() {
        return datasetFile;
    }

    public void setDatasetFile(String datasetFile) {
        this.datasetFile = datasetFile;
    }

    public String getSensorMessageFields() {
        return sensorMessageFields;
    }

    public void setSensorMessageFields(String sensorMessageFields) {
        this.sensorMessageFields = sensorMessageFields;
    }

    public String getSensorIdColumnName() {
        return sensorIdColumnName;
    }

    public void setSensorIdColumnName(String sensorIdColumnName) {
        this.sensorIdColumnName = sensorIdColumnName;
    }

    public String getTimestampColumnName() {
        return timestampColumnName;
    }

    public void setTimestampColumnName(String timestampColumnName) {
        this.timestampColumnName = timestampColumnName;
    }
}
