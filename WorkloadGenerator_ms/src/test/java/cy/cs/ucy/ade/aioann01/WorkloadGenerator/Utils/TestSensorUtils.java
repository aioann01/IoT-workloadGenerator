package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.MOCK_SENSOR_PROTOTYPE;
import static org.junit.Assert.*;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FieldPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import org.junit.Test;


public class TestSensorUtils {


    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    private static final String RANDOM_INTEGER_FIELD_PROTOTYPE = "{\"name\":\"dummy\",\"type\":\"integer\",\"value\":{\"random\":{\"minValue\":0,\"maxValue\":100}}}";
    private static final String RANDOM_BOOLEAN_FIELD_PROTOTYPE = "{\"name\":\"dummy\",\"type\":\"boolean\",\"value\":{\"random\":{\"minValue\":0,\"maxValue\":100}}}";
    private static final String RANDOM_STRING_FIELD_PROTOTYPE = "{\"name\":\"dummy\",\"type\":\"string\",\"value\":{\"random\":{\"minValue\":0,\"maxValue\":100}}}";
    private static final String RANDOM_MINVALUE_NOT_NUMBER_FIELD_PROTOTYPE = "{\"name\":\"dummy\",\"type\":\"integer\",\"value\":{\"random\":{\"minValue\":\"test\",\"maxValue\":100}}}";
    private static final String RANDOM_MAXVALUE_NOT_NUMBER_FIELD_PROTOTYPE = "{\"name\":\"dummy\",\"type\":\"boolean\",\"value\":{\"random\":{\"minValue\":0,\"maxValue\":\"test\"}}}";
    private static final String OBJECT_FIELD_PROTOTYPE = "{\"name\":\"complexSensor\",\"type\":\"object\",\"value\":[{\"name\":\"temperature in Celcius\",\"type\":\"double\",\"unit\":\"°C\",\"value\":{\"distributions\":[{\"minValue\":20,\"maxValue\":25,\"probability\":0.05},{\"minValue\":25,\"maxValue\":30,\"probability\":0.25},{\"minValue\":30,\"maxValue\":35,\"probability\":0.7}]}},{\"name\":\"brightness\",\"type\":\"integer\",\"value\":{\"random\":{\"minValue\":0,\"maxValue\":5}}},{\"name\":\"humidity\",\"type\":\"integer\",\"unit\":\" %\",\"value\":{\"random\":{\"minValue\":0,\"maxValue\":100}}},{\"name\":\"motion\",\"type\":\"integer\",\"value\":{\"distributions\":[{\"value\":0,\"probability\":0.75},{\"value\":1,\"probability\":0.25}]}},{\"name\":\"motionDetection\",\"type\":\"boolean\",\"value\":{\"distributions\":[{\"value\":false,\"probability\":0.75},{\"value\":true,\"probability\":0.25}]}}]}";
    private static final String DISTRIBUTIONS_DOUBLE_FIELD_PROTOTYPE = "{\"name\":\"temperature in Celcius\",\"type\":\"double\",\"unit\":\"°C\",\"value\":{\"distributions\":[{\"minValue\":20,\"maxValue\":25,\"probability\":0.05},{\"minValue\":25,\"maxValue\":30,\"probability\":0.25},{\"minValue\":30,\"maxValue\":35,\"probability\":0.7}]}}";
    private static final String DISTRIBUTIONS_MIN_VALUE_NOT_NUMBER_FIELD_PROTOTYPE = "{\"name\":\"temperature in Celcius\",\"type\":\"double\",\"unit\":\"°C\",\"value\":{\"distributions\":[{\"minValue\":\"FAIL\",\"maxValue\":25,\"probability\":0.05},{\"minValue\":25,\"maxValue\":30,\"probability\":0.25},{\"minValue\":30,\"maxValue\":35,\"probability\":0.7}]}}";
    private static final String DISTRIBUTIONS_MAX_VALUE_NOT_NUMBER_FIELD_PROTOTYPE = "{\"name\":\"temperature in Celcius\",\"type\":\"double\",\"unit\":\"°C\",\"value\":{\"distributions\":[{\"minValue\":2,\"maxValue\":\"FAIL\",\"probability\":0.05},{\"minValue\":25,\"maxValue\":30,\"probability\":0.25},{\"minValue\":30,\"maxValue\":35,\"probability\":0.7}]}}";
    private static final String DISTRIBUTIONS_INTEGER_FIELD_PROTOTYPE = "{\"name\":\"motion\",\"type\":\"integer\",\"value\":{\"distributions\":[{\"value\":0,\"probability\":0.75},{\"value\":1,\"probability\":0.25}]}}";
    private static final String DISTRIBUTIONS_BOOLEAN_FIELD_PROTOTYPE = "{\"name\":\"motionDetection\",\"type\":\"boolean\",\"value\":{\"distributions\":[{\"value\":false,\"probability\":0.75},{\"value\":true,\"probability\":0.25}]}}";
    private static final String DISTRIBUTIONS_PROBABILITY_SUM_EXCEEDS_1_DOUBLE_FIELD_PROTOTYPE = "{\"name\":\"temperature in Celcius\",\"type\":\"double\",\"unit\":\"°C\",\"value\":{\"distributions\":[{\"minValue\":20,\"maxValue\":25,\"probability\":0.15},{\"minValue\":25,\"maxValue\":30,\"probability\":0.25},{\"minValue\":30,\"maxValue\":35,\"probability\":0.7}]}}";
    private static final String DISTRIBUTIONS_PROBABILITY_SUM_BELOW_1_DOUBLE_FIELD_PROTOTYPE = "{\"name\":\"temperature in Celcius\",\"type\":\"double\",\"unit\":\"°C\",\"value\":{\"distributions\":[{\"minValue\":20,\"maxValue\":25,\"probability\":0.05},{\"minValue\":25,\"maxValue\":30,\"probability\":0.05},{\"minValue\":30,\"maxValue\":35,\"probability\":0.7}]}}";
    private static final String CONSTANT_INTEGER_FIELD_PROTOTYPE = "{\"name\":\"dummyConstant\",\"type\":\"integer\",\"value\":{\"constant\":{\"value\":2}}}";
    private static final String NORMAL_DISTRIBUTION_DOUBLE_FIELD_PROTOTYPE = "{\"name\":\"dummyNormalDistribution\",\"type\":\"double\",\"value\":{\"normalDistribution\":{\"mean\":20,\"deviation\":5}},\"unit\":\"%\"}";
    private static final String NORMAL_DISTRIBUTION_MEAN_NOT_NUMBER_FIELD_PROTOTYPE = "{\"name\":\"dummyNormalDistribution\",\"type\":\"double\",\"value\":{\"normalDistribution\":{\"mean\":\"test\",\"deviation\":5}},\"unit\":\"%\"}";
    private static final String NORMAL_DISTRIBUTION_DEVIATION_NOT_NUMBER_FIELD_PROTOTYPE = "{\"name\":\"dummyNormalDistribution\",\"type\":\"double\",\"value\":{\"normalDistribution\":{\"mean\":2,\"deviation\":\"test\"}},\"unit\":\"%\"}";


    //Generation Rate creation


    @Test
    public void testProcessRandomGenerationRate() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(RANDOM_INTEGER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        assertNotNull("created GenerationRate is not null", generationRate);
        assertTrue("created GenerationRate is RandomGenerationRate", generationRate instanceof RandomGenerationRate);
    }


    @Test
    public void testProcessConstantGenerationRate() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(CONSTANT_INTEGER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        assertNotNull("created GenerationRate is not null", generationRate);
        assertTrue("created GenerationRate is ConstantGenerationRate", generationRate instanceof ConstantGenerationRate);
    }


    @Test
    public void testProcessDistributionsGenerationRate() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(DISTRIBUTIONS_INTEGER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        assertNotNull("created GenerationRate is not null", generationRate);
        assertTrue("created GenerationRate is DistributionGenerationRate", generationRate instanceof DistributionGenerationRate);
    }


    @Test
    public void testProcessNormalDistributionGenerationRate() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(NORMAL_DISTRIBUTION_DOUBLE_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        assertNotNull("created GenerationRate is not null", generationRate);
        assertTrue("created GenerationRate is NormalDistributionGenerationRate", generationRate instanceof NormalDistributionGenerationRate);
    }




    //Generation Rates Validations
//RandomGenerationRate validations
    @Test
    public void testRandomGenerationRate_NotNumberType() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(RANDOM_BOOLEAN_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.BOOLEAN);
        }catch (ValidationException ve){
            return;
        }
        fail("Validation Exception not thrown");
    }

    @Test
    public void testRandomGenerationRate_MinValueNotNumber() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(RANDOM_MINVALUE_NOT_NUMBER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        }catch (Exception ve){
            return;
        }
        fail("Exception not thrown");
    }

    @Test
    public void testRandomGenerationRate_MaxValueNotNumber() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(RANDOM_MAXVALUE_NOT_NUMBER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        }catch (Exception ve){
            return;
        }
        fail("Exception not thrown");
    }

    //ConstantGenerationRate validations
    @Test
    public void testConstantGenerationRate_WrongValueType() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(CONSTANT_INTEGER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.BOOLEAN);
        }catch (ValidationException ve){
            return;
        }
        fail("Validation Exception not thrown");
    }

    //NormalDistributionGenerationRate validations
    @Test
    public void testNormalDistributionGenerationRate_MeanValueNotNumber() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(NORMAL_DISTRIBUTION_MEAN_NOT_NUMBER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        }catch (Exception exception){
            return;
        }
        fail("Exception not thrown");
    }

    @Test
    public void testNormalDistributionGenerationRate_DeviationValueNotNumber() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(NORMAL_DISTRIBUTION_DEVIATION_NOT_NUMBER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        }catch (Exception exception){
            return;
        }
        fail("Exception not thrown");
    }

    //DistributionGenerationRate validations
    @Test
    public void testDistributionGenerationRate_SumOfProbabilitiesExceeds1() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(DISTRIBUTIONS_PROBABILITY_SUM_EXCEEDS_1_DOUBLE_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.DOUBLE);
        }catch (ValidationException validationException){
            return;
        }
        fail("Validation Exception not thrown");
    }

    @Test
    public void testDistributionGenerationRate_SumOfProbabilitiesUnder1() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(DISTRIBUTIONS_PROBABILITY_SUM_BELOW_1_DOUBLE_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.DOUBLE);
        }catch (ValidationException validationException){
            return;
        }
        fail("Validation Exception not thrown");
    }

    @Test
    public void testDistributionGenerationRate_WrongTypeForDistributionValue() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(DISTRIBUTIONS_BOOLEAN_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.STRING);
        }catch (ValidationException validationException){
            return;
        }
        fail("Validation Exception not thrown");
    }

    @Test
    public void testDistributionGenerationRate_MinValueNotNumber() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(DISTRIBUTIONS_MIN_VALUE_NOT_NUMBER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        }catch (Exception exception){
            return;
        }
        fail("Exception not thrown");
    }

    @Test
    public void testDistributionGenerationRate_MaxValueNotNumber() throws Exception {
        FieldPrototype fieldPrototype = mapper.readValue(DISTRIBUTIONS_MAX_VALUE_NOT_NUMBER_FIELD_PROTOTYPE, new TypeReference<FieldPrototype>(){});
        try{
            GenerationRate generationRate = SensorUtils.processAndValidateGenerationRate(fieldPrototype.getValue(), TypesEnum.INTEGER);
        }catch (Exception exception){
            return;
        }
        fail("Exception not thrown");
    }


//Generation Rate value generation
    //RandomGenerationRate
    @Test
    public void testRandomGenerationRateIntegerValueGeneration() throws Exception {
        RandomGenerationRate randomGenerationRate = new RandomGenerationRate();
        randomGenerationRate.setMinValue(0);
        randomGenerationRate.setMaxValue(100);
        Integer generatedValue = (Integer)SensorUtils.generateGenerationRateValue(randomGenerationRate, TypesEnum.INTEGER);
        assertNotNull("RandomGenerationRate Integer generated value ", generatedValue);
        assertTrue("RandomGenerationRate Integer generated value between range", generatedValue >= 0 && generatedValue <= 100);
    }

    @Test
    public void testRandomGenerationRateDoubleValueGeneration() throws Exception {
        RandomGenerationRate randomGenerationRate = new RandomGenerationRate();
        randomGenerationRate.setMinValue(0.0);
        randomGenerationRate.setMaxValue(100.0);
        Double generatedValue = (Double)SensorUtils.generateGenerationRateValue(randomGenerationRate, TypesEnum.DOUBLE);
        assertNotNull("RandomGenerationRate Double generated value ", generatedValue);
        assertTrue("RandomGenerationRate Double generated value between range", generatedValue >= 0.0 && generatedValue <= 100.0);
    }



    //ConstantGenerationRate
    @Test
    public void testConstantGenerationRateIntegerValueGeneration() throws Exception {
        ConstantGenerationRate constantGenerationRate = new ConstantGenerationRate();
        constantGenerationRate.setValue(2);
        Integer generatedValue = (Integer)SensorUtils.generateGenerationRateValue(constantGenerationRate, TypesEnum.INTEGER);
        assertNotNull("ConstantGenerationRate generated Integer value", generatedValue);
        assertTrue("ConstantGenerationRate Integer generated value", generatedValue.equals(2));
    }

    @Test
    public void testConstantGenerationRateDoubleValueGeneration() throws Exception {
        ConstantGenerationRate constantGenerationRate = new ConstantGenerationRate();
        constantGenerationRate.setValue(2.0);
        Double generatedValue = (Double)SensorUtils.generateGenerationRateValue(constantGenerationRate, TypesEnum.DOUBLE);
        assertNotNull("ConstantGenerationRate generated Double value", generatedValue);
        assertTrue("ConstantGenerationRate Double generated value", generatedValue.equals(2.0));
    }

    @Test
    public void testConstantGenerationRateBooleanValueGeneration() throws Exception {
        ConstantGenerationRate constantGenerationRate = new ConstantGenerationRate();
        constantGenerationRate.setValue(true);
        Boolean generatedValue = (Boolean)SensorUtils.generateGenerationRateValue(constantGenerationRate, TypesEnum.BOOLEAN);
        assertNotNull("ConstantGenerationRate generated Boolean value", generatedValue);
        assertTrue("ConstantGenerationRate Boolean generated value", generatedValue.equals(true));
    }

    @Test
    public void testConstantGenerationRateStringValueGeneration() throws Exception {
        ConstantGenerationRate constantGenerationRate = new ConstantGenerationRate();
        constantGenerationRate.setValue("test");
        String generatedValue = (String)SensorUtils.generateGenerationRateValue(constantGenerationRate, TypesEnum.STRING);
        assertNotNull("ConstantGenerationRate generated String value", generatedValue);
        assertTrue("ConstantGenerationRate String generated value", generatedValue.equals("test"));
    }

    //NormalDistributionGenerationRate?

//    @Test
//    public void testNormalDistributionGenerationRateIntegerValueGeneration() throws Exception {
//        NormalDistributionGenerationRate normalDistributionGenerationRate = new NormalDistributionGenerationRate();
//        normalDistributionGenerationRate.setMean(20);
//        normalDistributionGenerationRate.setDeviation(5);
//        Integer generatedValue = (Integer)SensorUtils.generateGenerationRateValue(normalDistributionGenerationRate, TypesEnum.INTEGER);
//        assertNotNull("NormalDistributionGenerationRate Integer generated value ", generatedValue);
//        assertTrue("NormalDistributionGenerationRate Integer generated value", generatedValue.equals("test"));
//    }



    //DistributionGenerationRate?

//    @Test
//    public void testDistributionGenerationRateIntegerValueGeneration() throws Exception {
//        DistributionGenerationRate distributionGenerationRate = new DistributionGenerationRate();
//        Distribution d1 = new Distribution();
//        d1.setMinValue(0);
//        distributionGenerationRate.setDistributions();setMean(20);
//        distributionGenerationRate.setDeviation(5);
//        Integer generatedValue = (Integer)SensorUtils.generateGenerationRateValue(distributionGenerationRate, TypesEnum.INTEGER);
//        assertNotNull("DistributionGenerationRate Integer generated value ", generatedValue);
//        assertTrue("DistributionGenerationRate Integer generated value", generatedValue.equals("test"));
//    }





}
