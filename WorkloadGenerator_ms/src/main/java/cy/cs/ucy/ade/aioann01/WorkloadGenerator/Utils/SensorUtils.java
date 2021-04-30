package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.ScenarioFieldValueInfo;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.DatasetSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.SensorPrototype;
import org.codehaus.jettison.json.JSONObject;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.OutputFileEnum.CSV;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;

public class SensorUtils<T> {

    private static final Logger log = LoggerFactory.getLogger(SensorUtils.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static NumberComparator numberComparator = new NumberComparator();


    public static void setFieldPrototypeValue(FieldPrototype fieldPrototype) throws RuntimeException {
        String errorMessage = null;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (fieldPrototype.getTypeEnum().equals(OBJECT)) {
                ArrayList<LinkedHashMap<String, Object>> fieldPrototypes = (ArrayList<LinkedHashMap<String, Object>>) fieldPrototype.getValue();
                List<FieldPrototype> fieldPrototypeList = fieldPrototypes.stream()
                        .map(fieldPrototypeLinkedHashMap -> objectMapper.convertValue(fieldPrototypeLinkedHashMap, FieldPrototype.class))
                        .map(fieldPrototype1 ->
                        {
                            setFieldTypeEnum(fieldPrototype1);
                            setFieldPrototypeValue(fieldPrototype1);
                            return fieldPrototype1;
                        })
                        .collect(Collectors.toList());
                fieldPrototype.setValue(fieldPrototypeList);
            }else {
                fieldPrototype.setValue(processAndValidateGenerationRate(fieldPrototype.getValue(), fieldPrototype.getTypeEnum()));
            }
        } catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            //throw exception;
        }
        if (errorMessage != null)
            throw new RuntimeException(errorMessage);
    }




    public static Object generateGenerationRateValue(Object generationRateValue, TypesEnum type){
        GenerationRate generationRate = (GenerationRate)(generationRateValue);
        return generationRate.generateValue(type);
    }



//    public static GenerationRate createAndValidateGenerationRate(Object generationRateObject, TypesEnum typesEnum) throws ValidationException{
//        String errorMessage = null;
//        LinkedHashMap<String, Object> initialFieldPrototypeValue = (LinkedHashMap<String, Object>) generationRateObject;
//        GenerationRate generationRate = null;
//        if (initialFieldPrototypeValue.containsKey("normalDistribution")) {
//            generationRate = objectMapper.convertValue(initialFieldPrototypeValue.get("normalDistribution"), NormalDistributionGenerationRate.class);
//        } else if (initialFieldPrototypeValue.containsKey("distributions")) {
//            generationRate = objectMapper.convertValue(initialFieldPrototypeValue.get("normalDistribution"), NormalDistributionGenerationRate.class);
//        }else if(initialFieldPrototypeValue.containsKey("constant")) {
//            generationRate = objectMapper.convertValue(initialFieldPrototypeValue.get("constant"), ConstantGenerationRate.class);
//        } else if (initialFieldPrototypeValue.containsKey("distributions")) {
//            generationRate = objectMapper.convertValue(generationRateObject, DistributionGenerationRate.class);
//        } else if (initialFieldPrototypeValue.containsKey("random")) {
//            generationRate = objectMapper.convertValue(initialFieldPrototypeValue.get("random"), RandomGenerationRate.class);
//        }
//        else {
//            errorMessage = "Not supported generation rate type";
//            log.error(errorMessage);
//        }
//        generationRate.processAndValidate(typesEnum);
////        if(generationRate == null)
////            throw new RuntimeException("Value of field with name {" + fieldPrototype.getName() + "} is either null or don't match provided type: " + fieldPrototype.getType());
//        return generationRate;
//    }


    public static GenerationRate processAndValidateGenerationRate(Object generationRateToBeProcessedObject, TypesEnum typesEnum) throws Exception{
        String errorMessage = null;
        GenerationRate generationRate = null;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LinkedHashMap<String, Object> generationRateToBeProcessedObjectLinkedHashMap = (LinkedHashMap<String, Object>) generationRateToBeProcessedObject;
            if(generationRateToBeProcessedObjectLinkedHashMap.get("value") != null)
                generationRateToBeProcessedObjectLinkedHashMap = (LinkedHashMap<String, Object>)generationRateToBeProcessedObjectLinkedHashMap.get("value");
            if (generationRateToBeProcessedObjectLinkedHashMap.containsKey("normalDistribution")) {
                generationRate = objectMapper.convertValue(generationRateToBeProcessedObjectLinkedHashMap.get("normalDistribution"), NormalDistributionGenerationRate.class);
            } else if (generationRateToBeProcessedObjectLinkedHashMap.containsKey("distributions")) {
                generationRate = objectMapper.convertValue(generationRateToBeProcessedObjectLinkedHashMap, DistributionGenerationRate.class);
            }else if(generationRateToBeProcessedObjectLinkedHashMap.containsKey("constant")) {
                generationRate = objectMapper.convertValue(generationRateToBeProcessedObjectLinkedHashMap.get("constant"), ConstantGenerationRate.class);
            } else if (generationRateToBeProcessedObjectLinkedHashMap.containsKey("distributions")) {
                generationRate = objectMapper.convertValue(generationRateToBeProcessedObject, DistributionGenerationRate.class);
            } else if (generationRateToBeProcessedObjectLinkedHashMap.containsKey("random")) {
                generationRate = objectMapper.convertValue(generationRateToBeProcessedObjectLinkedHashMap.get("random"), RandomGenerationRate.class);
            }
            else {
                errorMessage = "Not supported generation rate type";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }
            generationRate.processAndValidate(typesEnum);
            return generationRate;
        } catch (ValidationException validationException) {
            log.error(VALIDATION_EXCEPTION_CAUGHT + validationException.getMessage(), validationException);
            throw new ValidationException("Could not process Generation Rate:"+validationException.getMessage());
        }  catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            throw new Exception("Could not process Generation Rate:" + exception.getMessage());
        }
    }



    public static Number validateNumberValueType(TypesEnum type, Number number) {
        if (type.equals(DOUBLE) && number instanceof Integer)
            number = number.doubleValue();
        return castValue(number, type) == null ? null : number;

    }

    public static boolean isValueOfType(TypesEnum type, Object value) {
        return castValue(value, type) == null ? false : true;

    }


    public static void setDatasetSensorPrototypeMessageType(DatasetSensorPrototype datasetSensorPrototype) throws Exception {
        if (datasetSensorPrototype.getMessageExportType() == null) {
            log.warn("sensorMessagePrototype type for datasetSensorPrototype {" + datasetSensorPrototype.getSensorPrototypeName() + "} has not been provided. Json type will be used as default");
            datasetSensorPrototype.setSensorMessageEnum(JSON);
            return;
        }
        switch (datasetSensorPrototype.getMessageExportType().toLowerCase()) {
            case "xml":
                datasetSensorPrototype.setSensorMessageEnum(XML);
                break;
            case "json":
                datasetSensorPrototype.setSensorMessageEnum(JSON);
                break;
            case "txt":
                datasetSensorPrototype.setSensorMessageEnum(TEXT);
                break;
            default:
                throw new Exception("Unsupported sensorMessagePrototype type for sensorPrototype.Supported types are : {json,xml,txt}");
        }

    }


    public static void processSensorMessagePrototypeType(SensorPrototype sensorPrototype) throws Exception {
        if (sensorPrototype.getMessagePrototype().getType() == null) {
            log.warn("sensorMessagePrototype type for sensorPrototype has not been provided. Json type will be used as default");
            sensorPrototype.getMessagePrototype().setSensorMessageType(JSON);
            return;
        }
        switch (sensorPrototype.getMessagePrototype().getType().toLowerCase()) {
            case "xml":
                sensorPrototype.getMessagePrototype().setSensorMessageType(XML);
                break;
            case "json":
                sensorPrototype.getMessagePrototype().setSensorMessageType(JSON);
                break;
            case "txt":
                sensorPrototype.getMessagePrototype().setSensorMessageType(TEXT);
                break;
            default:
                throw new Exception("Unsupported sensorMessagePrototype type for sensorPrototype.Supported types are : {json,xml,txt}");
        }
    }


    public static void createSensorPrototypeMessagePrototype(SensorPrototype sensorPrototype) throws Exception {
        SensorMessagePrototype sensorMessagePrototype = sensorPrototype.getMessagePrototype();
        if (sensorMessagePrototype != null || sensorPrototype instanceof DatasetSensorPrototype) {
            if (sensorPrototype instanceof MockSensorPrototype || (sensorPrototype instanceof DatasetSensorPrototype && sensorPrototype.getGenerationRate() != null))
                sensorPrototype.setGenerationRate(SensorUtils.processAndValidateGenerationRate(sensorPrototype.getGenerationRate(), INTEGER));
            if (sensorPrototype instanceof MockSensorPrototype || (sensorPrototype instanceof DatasetSensorPrototype && sensorPrototype.getMessagePrototype() != null))
                SensorUtils.processSensorMessagePrototypeType(sensorPrototype);
            if (sensorMessagePrototype != null) {
                List<FieldPrototype> fieldPrototypes = sensorMessagePrototype.getFieldsPrototypes();
                if (fieldPrototypes != null && !fieldPrototypes.isEmpty()) {
                    for (FieldPrototype fieldPrototype : fieldPrototypes) {
                        setFieldTypeEnum(fieldPrototype);
                        if (fieldPrototype.getTypeEnum() == null)
                            throw new Exception("Could not create type for field:" + fieldPrototype.getName() + " of SensorPrototype: " + sensorPrototype.getSensorPrototypeName() +
                                    ".Unknown type in configs.Acceptable types are:{string,boolean,number,double,object}." +
                                    "See documentation for more info");
                        setFieldPrototypeValue(fieldPrototype);
                    }
                }
            }
        }

    }

    public static String createFinalMessageFromDatasetAndMockData(JSONObject recordFields, SensorMessagePrototype mockSensorMessageFields, SensorMessageEnum messageType) {
        if (mockSensorMessageFields != null && mockSensorMessageFields.getFieldsPrototypes() != null && !mockSensorMessageFields.getFieldsPrototypes().isEmpty()) {
            for (FieldPrototype extraFieldPrototype : mockSensorMessageFields.getFieldsPrototypes()) {
                try {
                    recordFields.put(extraFieldPrototype.getName(), setFieldValue(extraFieldPrototype, null, false));
                } catch (Exception e) {
                    log.error("Could not create mock field value for " + extraFieldPrototype.getName(), e);
                }
            }
        }
        String formattedMessage = null;
        if (messageType.equals(JSON))
            formattedMessage = recordFields.toString();
        else if (messageType.equals(XML)) {
            formattedMessage = convertJSONtoXMLString(recordFields);
        } else
            formattedMessage = recordFields.toString();
        return formattedMessage;
    }


    public static String createMessage(SensorMessagePrototype messagePrototype, SensorFieldStatistics[] sensorFieldStatistics, Boolean evaluateFieldValue, Boolean scenarioMode, Map<String, ScenarioFieldValueInfo> scenarioFieldValueInfoMap) {
        JSONObject message = new JSONObject();
        for (int i = 0; i < messagePrototype.getFieldsPrototypes().size(); ++i) {
            FieldPrototype fieldPrototype = messagePrototype.getFieldsPrototypes().get(i);
            try {
                Object fieldValue = null;
                if(scenarioMode && scenarioFieldValueInfoMap.get(fieldPrototype.getName()) != null){
                    ScenarioFieldValueInfo scenarioFieldValueInfo = scenarioFieldValueInfoMap.get(fieldPrototype.getName());
                    FieldPrototype scenarioFieldPrototype = fieldPrototype.copyFieldPrototype();
                    scenarioFieldPrototype.setValue(scenarioFieldValueInfo.getSensorFieldScenarioGenerationRate());
                    fieldValue = setFieldValue(scenarioFieldPrototype, sensorFieldStatistics[i], evaluateFieldValue);
                }
                else
                    fieldValue = setFieldValue(fieldPrototype, sensorFieldStatistics[i], evaluateFieldValue);
                message.put(fieldPrototype.getName(), fieldValue);

            } catch (Exception e) {
                log.error("Could not create mock field value for " + fieldPrototype.getName(), e);
            }
        }

        String formattedMessage = null;
        if (messagePrototype.getSensorMessageType().equals(JSON))
            formattedMessage = message.toString();
        else if ((messagePrototype.getSensorMessageType().equals(XML))) {
            // org.json.JSONObject   jsonObjectMessage = new org.json.JSONObject(message.toString());
            formattedMessage = SensorUtils.convertJSONtoXMLString(message);
        } else
            formattedMessage = message.toString();
        return formattedMessage;

    }


    public static String convertJSONtoXMLString(JSONObject jsonObject) {
        try {
            String xmlString = "";
            if (jsonObject == null)
                return xmlString;
            Iterator<String> fields = jsonObject.keys();
            while (fields.hasNext()) {
                String field = processXMLValue(fields.next());
                JSONObject childJSONObject = jsonObject.optJSONObject(field);
                String value = null;
                if (childJSONObject == null) {
                    value = jsonObject.optString(field);
                    if (value != null)
                        value = processXMLValue(value);
                    else
                        value = "";
                } else {
                    value = convertJSONtoXMLString(childJSONObject);
                }
                xmlString = xmlString + "<" + field + ">" + value + "</" + field + ">";
            }
            return xmlString;
        } catch (Exception e) {
            log.error("Exception caught when converting JSON to xml", e);
            return "";
        }
    }


    public static String processXMLValue(String value) {
        String validXMLString = value;
        String xmlInvalidCharsRegex = "[(^/)]";
        if (value.contains("<"))
            validXMLString = validXMLString.replaceAll("<", "&lt;");
        if (value.contains(">"))
            validXMLString = validXMLString.replaceAll(">", "&gt;");
        if (value.contains("&"))
            validXMLString = validXMLString.replaceAll("&", "&amp;");
        if (value.contains("'"))
            validXMLString = validXMLString.replaceAll("'", "&apos;");
        if (value.contains("\""))
            validXMLString = validXMLString.replaceAll("\"", "&quot;");
        if (value.contains(" "))
            validXMLString = validXMLString.replaceAll(" ", "_");
        validXMLString = validXMLString.replaceAll(xmlInvalidCharsRegex, "");
        return validXMLString;
    }

    public static Object setFieldValue(FieldPrototype fieldPrototype, SensorFieldStatistics sensorFieldStatistics, Boolean evaluateFieldValue) {
        JSONObject fieldValueJSONObject = new JSONObject();
        if (!fieldPrototype.getTypeEnum().equals(OBJECT)) {
            //  try {
            String fieldValue = createFieldValue(fieldPrototype).toString();
            if (evaluateFieldValue) {
                SensorUtils.saveFieldStatistics(sensorFieldStatistics, fieldValue);
                //}
            }

            return fieldValue + (fieldPrototype.getUnit() == null
                    ? ""
                    : " " + fieldPrototype.getUnit());/*fieldValue.put(fieldPrototype.getName(),createFieldValue(fieldPrototype));*/
//            } catch (JSONException e) {
//                log.warn("Could not create messsage for field :"+fieldPrototype.getName());
//
//            }
        } else {
            for (FieldPrototype fieldPrototype1 : (ArrayList<FieldPrototype>) fieldPrototype.getValue()) {
                try {
                    fieldValueJSONObject.put(fieldPrototype1.getName(), setFieldValue(fieldPrototype1, null, false));
                } catch (Exception e) {
                    log.warn("Could not create messsage for field :" + fieldPrototype.getName());
                }
            }
        }
        return fieldValueJSONObject;
    }


    public static Object createFieldValue(FieldPrototype fieldPrototype) {
        GenerationRate generationRate = (GenerationRate)(fieldPrototype.getValue());
        return generationRate.generateValue(fieldPrototype.getTypeEnum());
    }


    public static void setFieldTypeEnum(FieldPrototype fieldPrototype) {
        String type = fieldPrototype.getType();
        switch (type.toLowerCase()) {
            case "string":
                fieldPrototype.setTypeEnum(STRING);
                break;
            case "integer":
                fieldPrototype.setTypeEnum(INTEGER);
                break;
            case "boolean":
                fieldPrototype.setTypeEnum(BOOLEAN);
                break;
            case "double":
                fieldPrototype.setTypeEnum(DOUBLE);
                break;
            case "object":
                fieldPrototype.setTypeEnum(OBJECT);
                break;

        }
    }


    public static <T> T castValue(Object value, TypesEnum type) {
        switch (type) {
            case INTEGER:
                return (T) castToType(value, Integer.class);
            case BOOLEAN:
                return (T) castToType(value, Boolean.class);
            case DOUBLE:
                return (T) castToType(value, Double.class);
            case STRING:
                return (T) castToType(value, String.class);
            default:
                return null;
        }
    }


    public static <T> T castToType(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        } else
            return null;
    }


    public static SensorFieldStatistics createSensorFieldValueStatistics(FieldPrototype fieldPrototype) {
        if (fieldPrototype != null) {
            SensorFieldStatistics sensorFieldStatistics = new SensorFieldStatistics();
            sensorFieldStatistics.setFieldName(fieldPrototype.getName());
            sensorFieldStatistics.setSumOfGenerationRate(0);
            switch (fieldPrototype.getTypeEnum()) {

                case INTEGER:
                    sensorFieldStatistics.setType(INTEGER);
                    sensorFieldStatistics.setSum(0.0);
                    sensorFieldStatistics.setMinValue(Integer.MAX_VALUE);
                    sensorFieldStatistics.setMaxValue(Integer.MIN_VALUE);
                    sensorFieldStatistics.setSampleValues(new ArrayList<>());
                    break;
                case DOUBLE:
                    sensorFieldStatistics.setType(TypesEnum.DOUBLE);
                    sensorFieldStatistics.setSum(0.0);
                    sensorFieldStatistics.setMinValue(Double.MAX_VALUE);
                    sensorFieldStatistics.setMaxValue(Double.MIN_VALUE);
                    sensorFieldStatistics.setSampleValues(new ArrayList<>());
                    break;
                case BOOLEAN:
                    sensorFieldStatistics.setType(TypesEnum.BOOLEAN);
                    sensorFieldStatistics.setFalseCount(0);
                    sensorFieldStatistics.setTrueCount(0);
                default:
                    sensorFieldStatistics.setType(TypesEnum.STRING);
            }
            return sensorFieldStatistics;
        } else
            return null;
    }

    public static SensorFieldStatistics createSensorFieldValueStatistics(String fieldValue, String fieldName) {
        if (fieldValue != null) {
            SensorFieldStatistics sensorFieldStatistics = new SensorFieldStatistics();
            sensorFieldStatistics.setFieldName(fieldName);
            sensorFieldStatistics.setSumOfGenerationRate(0);
//            if (Utils.isStringInteger(fieldValue)) {
//                sensorFieldStatistics.setType(INTEGER);
//                sensorFieldStatistics.setSum(0.0);
//                sensorFieldStatistics.setMinValue(Integer.MAX_VALUE);
//                sensorFieldStatistics.setMaxValue(Integer.MIN_VALUE);
//                sensorFieldStatistics.setSampleValues(new ArrayList<>());
//            }
//            else
            if (CommonUtils.isStringDouble(fieldValue)) {
                sensorFieldStatistics.setType(TypesEnum.DOUBLE);
                sensorFieldStatistics.setSum(0.0);
                sensorFieldStatistics.setMinValue(Double.MAX_VALUE);
                sensorFieldStatistics.setMaxValue(Double.MIN_VALUE);
                sensorFieldStatistics.setSampleValues(new ArrayList<>());

            } else if (CommonUtils.isStringBoolean(fieldValue)) {
                sensorFieldStatistics.setType(TypesEnum.BOOLEAN);
                sensorFieldStatistics.setFalseCount(0);
                sensorFieldStatistics.setTrueCount(0);
            } else {
                sensorFieldStatistics.setType(TypesEnum.STRING);
            }
            return sensorFieldStatistics;
        } else
            return null;
    }


    public static void saveFieldStatistics(SensorFieldStatistics valuesStatistics, String fieldValue) {
        valuesStatistics.setSamplesCount(valuesStatistics.getSamplesCount() + 1);
        try {
            switch (valuesStatistics.getType()) {
                case DOUBLE:
                case INTEGER:
                    Number numberValue = CommonUtils.castToNumber(fieldValue, valuesStatistics.getType());
                    if (numberComparator.compare(numberValue, valuesStatistics.getMinValue()) < 0) {
                        valuesStatistics.setMinValue(numberValue);
                    }
                    if (numberComparator.compare(numberValue, valuesStatistics.getMaxValue()) > 0) {
                        valuesStatistics.setMaxValue(numberValue);
                    }
                    valuesStatistics.setSum(valuesStatistics.getSum() + numberValue.doubleValue());
                    valuesStatistics.getSampleValues().add(numberValue);
                    break;
                case BOOLEAN:
                    if (fieldValue.toLowerCase().equals("false"))
                        valuesStatistics.setFalseCount(valuesStatistics.getFalseCount() + 1);
                    else
                        valuesStatistics.setTrueCount(valuesStatistics.getTrueCount() + 1);
                    break;
            }
        } catch (Exception exception) {
            log.error("Exception caught while saving statistics of field {" + valuesStatistics.getFieldName() + "} :" + exception.getMessage());
        }
    }

    public static Boolean isTypeNumeric(TypesEnum typesEnum) {
        return typesEnum.equals(INTEGER) || typesEnum.equals(DOUBLE);
    }

    public static void mergeSensorFieldStatistics(SensorFieldStatistics sensorFieldStatistics1, SensorFieldStatistics sensorFieldStatistics2) {
        if (SensorUtils.isTypeNumeric(sensorFieldStatistics1.getType())) {
            sensorFieldStatistics1.setSum(sensorFieldStatistics1.getSum() + sensorFieldStatistics2.getSum());
            sensorFieldStatistics1.setSampleValues(
                    Stream.concat(sensorFieldStatistics1.getSampleValues().stream(), sensorFieldStatistics2.getSampleValues().stream())
                            .collect(Collectors.toList()));
            if (numberComparator.compare(sensorFieldStatistics1.getMinValue(), sensorFieldStatistics2.getMinValue()) > 0)
                sensorFieldStatistics1.setMinValue(sensorFieldStatistics2.getMinValue());
            if (numberComparator.compare(sensorFieldStatistics1.getMaxValue(), sensorFieldStatistics2.getMaxValue()) < 0)
                sensorFieldStatistics1.setMaxValue(sensorFieldStatistics2.getMaxValue());
        } else if (sensorFieldStatistics1.getType().equals(BOOLEAN)) {
            sensorFieldStatistics1.setTrueCount(sensorFieldStatistics1.getTrueCount() + sensorFieldStatistics2.getTrueCount());
            sensorFieldStatistics1.setFalseCount(sensorFieldStatistics1.getFalseCount() + sensorFieldStatistics2.getFalseCount());
        }
        sensorFieldStatistics1.setSamplesCount(sensorFieldStatistics1.getSamplesCount() + sensorFieldStatistics2.getSamplesCount());
    }


    public static void exportSensorPrototypes(SensorFieldStatistics[] datasetValuesStatistics, int recordsCount, String sensorProtoypeName, int totalWaitTime) {
        log.debug("****************Printing statistics*****************\nFieldName\tFieldType\tSum\tMinValue\tMaxValue\tTrueCount\tFalseCount");
        try {
            FileWriter csvWriter = new FileWriter(sensorProtoypeName + "_statistics.csv");

            FileWriter jsonWriter = new FileWriter(sensorProtoypeName + "_Configs.json");
            SensorPrototype sensorPrototype = new SensorPrototype();
            SensorMessagePrototype messagePrototype = new SensorMessagePrototype();

            csvWriter.append("FieldName,FieldType,Sum,MinValue,MaxValue,TrueCount,FalseCount,Avg,Count\n");


            List<FieldPrototype> fields = new ArrayList<>();
            for (int i = 0; i < datasetValuesStatistics.length; ++i) {
                TypesEnum fieldType = datasetValuesStatistics[i].getType();

                boolean isFieldNumeric = fieldType.equals(TypesEnum.INTEGER) || fieldType.equals(TypesEnum.DOUBLE);
                FieldPrototype fieldPrototype = new FieldPrototype();
                fieldPrototype.setName(datasetValuesStatistics[i].getFieldName());
                if (datasetValuesStatistics[i] != null)
                    csvWriter.append(datasetValuesStatistics[i].toCSVRecordString()
                            + (isFieldNumeric ? "," + (datasetValuesStatistics[i].getSum().doubleValue() / recordsCount) : "") + "," + recordsCount + "\n");
                fieldPrototype.setType(datasetValuesStatistics[i].getType().name().toLowerCase());
                if (isFieldNumeric) {
                    setNumericTypeGenerationRate(fieldPrototype, datasetValuesStatistics[i]);
                } else if (fieldType.equals(TypesEnum.BOOLEAN)) {
                    setBooleanTypeGenerationRate(fieldPrototype, datasetValuesStatistics[i]);
                } else {

                }//String
                fields.add(fieldPrototype);
            }
            messagePrototype.setFieldsPrototypes(fields);
            sensorPrototype.setMessagePrototype(messagePrototype);
            sensorPrototype.setSensorPrototypeName(sensorProtoypeName);
            Gson gson = new Gson();
            gson.toJson(sensorPrototype, jsonWriter);

            csvWriter.append("totalWaitTime:" + totalWaitTime + " avgWaitTime:" + (totalWaitTime * 1.0 / recordsCount));
            jsonWriter.flush();
            csvWriter.flush();
            jsonWriter.close();
            csvWriter.close();

        } catch (IOException ioException) {
            log.error("IOException caught while exporting datasetSensorPrototype :" + ioException.getMessage(), ioException);
        } catch (Exception exception) {
            log.error("Exception caught while exporting datasetSensorPrototype :" + exception.getMessage(), exception);
        }
    }

    public static void setBooleanTypeGenerationRate(FieldPrototype fieldPrototype, SensorFieldStatistics sensorFieldStatistics) {
        int trueValues = sensorFieldStatistics.getTrueCount();
        int falseValues = sensorFieldStatistics.getFalseCount();
        Distribution falseDistribution = new Distribution(false, falseValues * 1.0 / sensorFieldStatistics.getSamplesCount());
        Distribution trueDistribution = new Distribution(true, trueValues * 1.0 / sensorFieldStatistics.getSamplesCount());
        List<Distribution> booleanDistributions = new ArrayList<>();
        booleanDistributions.add(trueDistribution);
        booleanDistributions.add(falseDistribution);
        DistributionGenerationRate distributionGenerationRate = new DistributionGenerationRate(booleanDistributions);
        fieldPrototype.setValue(distributionGenerationRate);
    }

    public static void setNumericTypeGenerationRate(FieldPrototype fieldPrototype, SensorFieldStatistics sensorFieldStatistics) {
        if (sensorFieldStatistics.getMaxValue().equals(sensorFieldStatistics.getMinValue()))
            fieldPrototype.setValue(new ConstantGenerationRate(sensorFieldStatistics.getMaxValue()));
        else {
            int buckets = 0;
            try {
                buckets = Integer.parseInt(ApplicationPropertiesUtil.readPropertyFromConfigs(HISTOGRAM_BINS_NUMBER));
            } catch (Exception exception) {
                log.warn("No " + HISTOGRAM_BINS_NUMBER + " property found. Default is 3");
                buckets = 3;

            }
            int bucketsCount[] = new int[buckets];
            double bucketWidth = (sensorFieldStatistics.getMaxValue().doubleValue() - sensorFieldStatistics.getMinValue().doubleValue()) / buckets;
            for (Number randomValue : sensorFieldStatistics.getSampleValues()) {
                double currentBucketRange = sensorFieldStatistics.getMinValue().doubleValue() + bucketWidth;
                for (int i = 0; i < bucketsCount.length; ++i, currentBucketRange += bucketWidth) {
                    if (randomValue.doubleValue() <= currentBucketRange) {
                        bucketsCount[i]++;
                        break;
                    }
                }
            }
            double minBucketValue = sensorFieldStatistics.getMinValue().doubleValue();
            double maxBucketValue = minBucketValue + bucketWidth;
            List<Distribution> numericDistributions = new ArrayList<>();
            for (int i = 0; i < buckets; ++i) {
                Distribution bucketDistribution = new Distribution(minBucketValue, maxBucketValue, bucketsCount[i] * 1.0 / sensorFieldStatistics.getSamplesCount());
                numericDistributions.add(bucketDistribution);
                minBucketValue += bucketWidth;
                maxBucketValue += bucketWidth;
            }
            DistributionGenerationRate numericDistribution = new DistributionGenerationRate(numericDistributions);
            fieldPrototype.setValue(numericDistribution);
        }
    }

    public static String createCSVOutputRecord(MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo, FileRecord fileRecord) throws Exception{

        StringBuilder record = new StringBuilder();
        record.append(fileRecord.getDate() + mockSensorPrototypeOutputFileInfo.getMessageFieldsSeparator());
        record.append(fileRecord.getSensorId() + mockSensorPrototypeOutputFileInfo.getMessageFieldsSeparator());
        String recordValues = null;
        if(mockSensorPrototypeOutputFileInfo.getOutputFileEnum().equals(CSV)) {
            switch (mockSensorPrototypeOutputFileInfo.getSensorMessageEnum()) {
                case XML:
                    recordValues = createCSVOutputRecordValuesForXML(mockSensorPrototypeOutputFileInfo, fileRecord);
                    break;
                case JSON:
                    recordValues = createCSVOutputRecordValuesForJSON(mockSensorPrototypeOutputFileInfo, fileRecord);
                    break;
                case TEXT:
                default:
                    recordValues = fileRecord.getMessage().toString();
            }
        }
        else
            recordValues = fileRecord.getMessage().toString();
        record.append(recordValues);
        return record.toString();
    }

    public static String createCSVOutputRecordValuesForXML(MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo, FileRecord fileRecord){
        StringBuilder recordValues = new StringBuilder();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        String xmlMessageWithRootElement = ROOT_XML_ELEMENT_START + fileRecord.getMessage().toString() +ROOT_XML_ELEMENT_END;

        try{
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse(new InputSource(new StringReader(xmlMessageWithRootElement)));
            xmlDocument.getDocumentElement().normalize();
            Map<String, Node> firstLevelNodesMap = new HashMap<>();
            Node rootNode = xmlDocument.getFirstChild();
            NodeList childNodes = rootNode.getChildNodes();
            for(int i=0; i < childNodes.getLength(); ++i){
                Node node = childNodes.item(i);
                if(node != null) {
                    firstLevelNodesMap.put(node.getNodeName(),node);
                }
            }
            for (int i = 0; i < mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames().length; ++i) {
                Node node = firstLevelNodesMap.get(mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames()[i]);
                if(node != null && node.getTextContent() != null){
                    if(1 + i == mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames().length)
                        recordValues.append(
                                hasChildNodes(node)
                                        ?"Object"
                                        :node.getTextContent()
                        );
                    else
                        recordValues.append(
                                hasChildNodes(node)
                                        ?"Object"
                                        :node.getTextContent() + EXCEL_COLUMN_SEPARATOR
                        );
                }
            }
        }
        catch (Exception e){
            return fileRecord.getMessage().toString();
        }
        return recordValues.toString();
    }

    public static boolean hasChildNodes(Node node){
        return  (!node.hasChildNodes() || (node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeValue() != null))
                ?false
                :true;
    }

    public static String createCSVOutputRecordValuesForJSON(MockSensorPrototypeOutputFileInfo mockSensorPrototypeOutputFileInfo, FileRecord fileRecord){
        try {
            StringBuilder record = new StringBuilder();
            JSONObject messageJSONObject = new JSONObject(fileRecord.getMessage().toString().trim());
            for (int i = 0; i < mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames().length; ++i) {
                String columnValue = "";
                if (messageJSONObject.optJSONObject(mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames()[i]) != null)
                    columnValue = "Object";
                else {
                    columnValue = messageJSONObject.optString(mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames()[i]);
                }
                if (i + 1 == mockSensorPrototypeOutputFileInfo.getSensorMessageFieldNames().length)
                    record.append(columnValue);
                else
                    record.append(columnValue + mockSensorPrototypeOutputFileInfo.getMessageFieldsSeparator());
            }
            return record.toString();
        }
        catch (Exception e){
            return fileRecord.getMessage().toString();
        }
    }


}



