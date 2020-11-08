package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.externalsorting.csv.CsvExternalSort;
import com.google.code.externalsorting.csv.CsvSortOptions;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorFieldStatistics;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorMessagePrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.DatasetSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.KafkaSensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices.MqttSensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.ApplicationPropertiesUtil;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.GenerationRatesUtils;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.Utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.code.externalsorting.csv.CsvExternalSort.DEFAULTMAXTEMPFILES;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;


@Service
public class DatasetSensorPrototypeService {

    @Autowired
    public ApplicationPropertiesService applicationPropertiesService;

    private ObjectMapper mapper = new ObjectMapper();

    private ISensorMessageSendService sensorMessageSendService;

    private static final Logger log = LoggerFactory.getLogger(DatasetSensorPrototypeService.class);

    public ISensorMessageSendService getSensorMessageSendService() {
        return sensorMessageSendService;
    }

    public void setSensorMessageSendService(ISensorMessageSendService sensorMessageSendService) {
        this.sensorMessageSendService = sensorMessageSendService;
    }

    public DatasetSensorPrototype createDatasetSensorPrototype(DatasetSensorPrototype datasetSensorPrototype) throws Exception {
        String errorMessage = null;
        String datasetSensorPrototypeName = datasetSensorPrototype.getSensorPrototypeName();
        if (datasetSensorPrototypeName == null) {
            errorMessage = "sensorPrototypeName for datasetSensorPrototype has not been provided. It is a mandatory field. DatasetSensorPrototype will be ignored";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
        Optional<Boolean> timestampedDatasetOptional = Optional.ofNullable(datasetSensorPrototype.getTimestampedDataset());
        Optional<Boolean> exportStatisticsOptional = Optional.ofNullable(datasetSensorPrototype.getExportGenerationRate());
        if (!exportStatisticsOptional.isPresent()) {
            log.warn("ExportGenerationRate has not been provided. Default is false");
            datasetSensorPrototype.setExportGenerationRate(false);
        }
        if (!timestampedDatasetOptional.isPresent()) {
            errorMessage = "timestampedDataset of {" + datasetSensorPrototypeName + "} datasetSensorPrototype is null or not valid";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
        else if(timestampedDatasetOptional.get()){
            if(datasetSensorPrototype.getTimestampColumnName() == null){
                errorMessage = "DatasetSensorPrototype {" + datasetSensorPrototypeName + "} is timestamped but timestampColumnName has not be provided or not valid";
                log.error(errorMessage);
                throw new Exception(errorMessage);}
            if(datasetSensorPrototype.getTimestampFormat() == null){
                errorMessage = "DatasetSensorPrototype {" + datasetSensorPrototypeName + "} is timestamped but timestampFormat has not be provided or not valid";
                log.error(errorMessage);
                throw new Exception(errorMessage);}
            Optional<Boolean> sortedDataset = Optional.ofNullable(datasetSensorPrototype.isSortedDataset());

            if(!sortedDataset.isPresent()){
                errorMessage = "DatasetSensorPrototype {" + datasetSensorPrototypeName + "} is timestamped but sortedDataset field has not be provided or not valid";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }
        }
        else if(datasetSensorPrototype.getGenerationRate() == null){
            errorMessage = "DatasetSensorPrototype {"+datasetSensorPrototypeName+"} is not timestamped and does not have generationRate";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        try {
            SensorUtils.setDatasetSensorPrototypeMessageType(datasetSensorPrototype);
        }catch (Exception e){
            errorMessage = "Could not set export message type for datasetSensorPrototype {"+datasetSensorPrototype+"}";
            log.error(EXCEPTION_CAUGHT+errorMessage,e);
            throw new Exception(errorMessage);
        }

//        else if(!timestampedDatasetOptional.get() && datasetSensorPrototype.getGenerationRate() == null){
//            errorMessage = "datasetSensorPrototype  {" + datasetSensorPrototypeName + "} has n";
//            log.error(errorMessage);
//            throw new Exception(errorMessage);
//        }
        if (datasetSensorPrototype.getSensorMessageFields() == null)
            log.warn("sensorMessageFields of {" + datasetSensorPrototypeName + "} datasetSensorPrototype has not been provided. Default is all.");

        if (datasetSensorPrototype.getMessagePrototype() != null || datasetSensorPrototype.getGenerationRate() != null){
            SensorUtils.createSensorPrototypeMessagePrototype(datasetSensorPrototype);
        }
        if(datasetSensorPrototype.getMessagePrototype() != null && !datasetSensorPrototype.getSensorMessageEnum().equals(datasetSensorPrototype.getMessagePrototype().getSensorMessageType())){
            errorMessage = "Provided extra fields type {"+datasetSensorPrototype.getMessagePrototype().getSensorMessageType()+"} is not the same as datasetSensorPrototype datasetSensorPrototype {"+datasetSensorPrototype+"} export message type {"+datasetSensorPrototype.getSensorMessageEnum()+"}";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if(datasetSensorPrototype.getMessagePrototype() == null)
            log.warn("messagePrototype of {" + datasetSensorPrototypeName + "} datasetSensorPrototype has not been provided or is null. No mock extra fields will be created.");
//        else
//            log.warn("generationRate of {" + datasetSensorPrototypeName + "} datasetSensorPrototype has not been provided or is null. Timestamp will be used.");
        if (datasetSensorPrototype.getSensorIdColumnName() == null) {
            errorMessage = "sensorIdColumnName of {" + datasetSensorPrototypeName + "} datasetSensorPrototype is not provided. SensorPrototype  name will be used";
            log.warn(errorMessage);
            //throw new Exception(errorMessage);
        }
        return datasetSensorPrototype;
    }


    public File validateFile(Boolean isSorted, String csvFileName, String sensorColumnIdName, Boolean isTimestamped, String timestampColumnName, String datasetSensorPrototypeName, String timestampFormat){
        log.debug("Validating csv file for {"+ datasetSensorPrototypeName +"} datasetSensorPrototype");
        File file;
        if(csvFileName == null){
            log.error("datasetFile was not provided for {"+ datasetSensorPrototypeName +"} datasetSensorPrototype");
            return null;
        }
        String []str = csvFileName.split("\\.");
        if(str.length != 2){
            log.error("Provided file {"+csvFileName+"} is not valid file Name or does not have file extension.");
            return null;
        }
        if(!str[1].equals(EXCEL_FILENAME)){
            log.error("Provided file {"+csvFileName+"} is not CSV file.");
            return null;
        }
        try {
            String resourcesDirectory = ApplicationPropertiesUtil.getResourcesDirectory();
            file = new File(resourcesDirectory + csvFileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String firstLine = br.readLine();
            String[] columnNames = firstLine.split(EXCEL_COLUMN_SEPERATOR);
            if(columnNames[0].startsWith(UTF8_BOM))
                columnNames[0] = columnNames[0].substring(1);
            boolean sensorIdColumnNameFound = false;
            boolean timestampColumnNameFound = false;
            int timestampColumnIndex = 0;
            for(int i=0; i <columnNames.length; ++i){
                if(sensorIdColumnNameFound && timestampColumnNameFound)
                    break;
                if(columnNames[i].equals(sensorColumnIdName))
                    sensorIdColumnNameFound = true;
                if(columnNames[i].equals(timestampColumnName)){
                    timestampColumnNameFound = true;
                    timestampColumnIndex = i;
                }
            }
            if(sensorColumnIdName == null || sensorIdColumnNameFound){
                if(isTimestamped && !timestampColumnNameFound){
                    log.error("DatasetSensorPrototype {"+datasetSensorPrototypeName+"} is timestamped but timestampColumnName {"+timestampColumnName+"} not found in the first line of the CSV.");
                    return null;
                }
                if(isTimestamped && !isSorted){
                    File sortedFile = null;
                    try {
                        String trimmedDatasetName = file.getName().replace(".csv","");
                        sortedFile = sortCSVDataset(file, trimmedDatasetName + "_sorted.csv", timestampColumnIndex, timestampFormat);
                        if(sortedFile == null){
                            log.error("DatasetSensorPrototype {"+datasetSensorPrototypeName+"} is timestamped but could not be sorted.");
                            return null;
                        }
                    }catch (Exception exception){
                        log.error("DatasetSensorPrototype {"+datasetSensorPrototypeName+"} is timestamped but could not be sorted.");
                        return null;
                    }
                    return sortedFile;
                }
                return file;//Valid file
            }
            else {
                log.error("SensorIdColumnName {"+sensorColumnIdName+"} not found in the first line of the CSV.");
                return null;
            }
        } catch (IOException e) {
            log.error("csv dataset file '"+csvFileName +"' could not be read \n"+EXCEPTION_CAUGHT+e.getMessage());
            return null;
        }
        catch (Exception e){
            log.error("Exception caught: "+e.getMessage(),e);
            return null;
        }
    }

    private Comparator<CSVRecord> createCSVRecordTimestampComparator(String timestampFormat, Integer timestampColumnIndex){
        SimpleDateFormat formatter = new SimpleDateFormat(timestampFormat);
        return (op1, op2) -> {
            try {
                return formatter.parse(op1.get(timestampColumnIndex)).compareTo(formatter.parse(op2.get(timestampColumnIndex)));
            } catch (ParseException e){
                return op1.get(timestampColumnIndex).compareTo(op2.get(timestampColumnIndex));
            }
        };
    }

    public File sortCSVDataset(File file, String outputName, int timestampColumnIndex, String timestampFormat){
        Comparator<CSVRecord> comparator = createCSVRecordTimestampComparator(timestampFormat, timestampColumnIndex);
        CsvSortOptions sortOptions = new CsvSortOptions
                .Builder(CsvExternalSort.DEFAULTMAXTEMPFILES, comparator, 1, CsvExternalSort.estimateAvailableMemory())
                .charset(Charset.defaultCharset())
                .distinct(false)
                .numHeader(1)
                .skipHeader(false)
                .format(CSVFormat.DEFAULT)
                .build();
        File sortedFile = new File(outputName);
        try {
            sortedFile.delete();
            sortedFile.createNewFile();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        List<File> sortInBatch = null;
        try {
            sortInBatch = CsvExternalSort.sortInBatch(file, null, sortOptions);
        } catch (IOException ioException) {
            log.error("Exception caught while sorting the dataset:"+ioException.getMessage(),ioException);
            return null;
        }
        try {
            CsvExternalSort.mergeSortedFiles(sortInBatch, sortedFile, sortOptions, true);
            return sortedFile;
        } catch (IOException ioException) {
            log.error("Exception caught while merging sorted files for dataset:"+ioException.getMessage(),ioException);
            return null;
        } catch (ClassNotFoundException e) {
            log.error("Exception caught while sorting the dataset:"+e.getMessage(),e);
            return null;
        }
        catch (Exception exception){
            log.error("Exception caught while sorting the dataset:"+exception.getMessage(),exception);
            return null;
        }
    }


    public long calculateNextMessageTimeForGenerationRate(Object generationRate, GenerationRateEnum generationRateEnum){
        try {
            return (Integer) (GenerationRatesUtils.generateGenerationRateValue(generationRate, TypesEnum.INTEGER, generationRateEnum));
        }catch(Exception exception){
            log.error("Could not cast calculated next sensor message time to integer.Default will be 1 s");
            return 1;
        }
    }

    public boolean isFieldToBeSend(String fieldsNames,String fieldName){
        if (fieldsNames == null)
            return  true;
        else{
            String fieldsToSend[] = fieldsNames.split(",");
            for(String field:fieldsToSend)
                if(field.equals(fieldName))
                    return true;
        }
        return false;
    }

    public String createDatasetSensorPrototypeMessage(Boolean exportDatasetStatistics, SensorFieldStatistics[] valuesStatistics, String datasetSensorPrototypeName, String [] fieldNames, String [] fieldValues, int sensorIdColumnIndex, int timestampColumnIndex, String sensorMessageFields, SensorMessagePrototype sensorMessagePrototype, SensorMessageEnum messageType){
        JSONObject csvRecordFieldsMessageJSONObject = new JSONObject();

        String finalMessage = null;
        try {

            for (int i = 0; i < fieldNames.length; ++i) {
                if (/*i != sensorIdColumnIndex && i != timestampColumnIndex && */isFieldToBeSend(sensorMessageFields, fieldNames[i]) &&   i < fieldValues.length) {
                    csvRecordFieldsMessageJSONObject.put(fieldNames[i], fieldValues[i]);
                    if(exportDatasetStatistics && valuesStatistics[i] != null){
                        SensorUtils.saveFieldStatistics(valuesStatistics[i], fieldValues[i]);

                    }
                }
            }
        }
        catch (Exception e){
            log.error("Could not create message for {"+datasetSensorPrototypeName+"} datasetSensorPrototype");
        }
        try {
            return  SensorUtils.createFinalMessageFromDatasetAndMockData(csvRecordFieldsMessageJSONObject, sensorMessagePrototype, messageType);
        }catch (Exception exception){
            log.error("Could not create finalMessage payload for {"+datasetSensorPrototypeName+"} due to "+exception.getMessage(),exception);
        }
        return finalMessage;
    }



    public void sendMessage(String sensorId, String message, SensorMessageEnum contentType) throws RuntimeException{
        try {
            sensorMessageSendService.sendMessage(sensorId, message, contentType);
        }catch (Exception exception){
            log.error("Error while sending sensor message: "+exception.getMessage(),exception);
        }

    }

}
