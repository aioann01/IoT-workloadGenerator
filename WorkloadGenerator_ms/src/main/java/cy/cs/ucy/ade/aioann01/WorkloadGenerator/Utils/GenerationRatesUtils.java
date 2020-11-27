package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FieldPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData.MockSensor;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.SensorPrototype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Random;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum.RANDOM;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.EXCEPTION_CAUGHT;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.validateNumberValueType;

public class GenerationRatesUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(GenerationRatesUtils.class);


    public static DistributionGenerationRate validateDistributionGenerationRateValue(Object generationRateValue, TypesEnum type){
        DistributionGenerationRate updatedDistributionGenerationRate = (DistributionGenerationRate)generationRateValue;
        double probabilitySum = 0;
        for(Distribution distribution: updatedDistributionGenerationRate.getDistributions()) {
            if(type.equals(BOOLEAN)){
                if(distribution.getValue() == null || !validateValueType(type,distribution.getValue()))
                    return null;
            }
            else{
                Number updatedMinValue = validateNumberValueType(type,distribution.getMinValue());
                Number updatedMaxValue = validateNumberValueType(type,distribution.getMaxValue());
                if(updatedMaxValue == null || updatedMinValue == null || distribution.getProbability() == null || !(distribution.getProbability() instanceof Double))
                    return null;
                else{
                    distribution.setMinValue(updatedMinValue);
                    distribution.setMaxValue(updatedMaxValue);
                }
            }
            probabilitySum += distribution.getProbability();}
        if(probabilitySum < 1.0)
            return null;
        return updatedDistributionGenerationRate;
    }

    public static NormalDistributionGenerationRate validateNormalDistributionGenerationRateValue(Object generationRateValue, TypesEnum type){
        NormalDistributionGenerationRate updatedNormalDistributionGenerationRate = (NormalDistributionGenerationRate)generationRateValue;
        Number updatedMean = validateNumberValueType(type,updatedNormalDistributionGenerationRate.getMean());
        Number updatedDeviation = validateNumberValueType(type,updatedNormalDistributionGenerationRate.getDeviation());
        if(updatedMean == null || updatedDeviation == null)
            return  null;
        else{
            updatedNormalDistributionGenerationRate.setMean(updatedMean);
            updatedNormalDistributionGenerationRate.setDeviation(updatedDeviation);
        }
        return updatedNormalDistributionGenerationRate;
    }

    public static boolean validateConstantGenerationRateValue(TypesEnum type, Object value){
        ConstantGenerationRate constantGenerationRate = (ConstantGenerationRate)value;
        if(constantGenerationRate.getValue() == null || !validateValueType(type, ((ConstantGenerationRate) value).getValue()))
            return false;
        return true;

    }

    public static RandomGenerationRate validateRandomGenerationRateValue(Object generationRateValue, TypesEnum type){
        RandomGenerationRate updatedRandomGenerationRate = (RandomGenerationRate)generationRateValue;
        if(type.equals(DOUBLE) || type.equals(INTEGER)){
            Number updatedMinValue = validateNumberValueType(type,updatedRandomGenerationRate.getMinValue());
            Number updatedMaxValue = validateNumberValueType(type,updatedRandomGenerationRate.getMaxValue());
            if(updatedMinValue == null || updatedMaxValue == null)
                return null;
            else{
                updatedRandomGenerationRate.setMaxValue(updatedMaxValue);
                updatedRandomGenerationRate.setMinValue(updatedMinValue);
            }}
        return updatedRandomGenerationRate;
    }



    public static <T>T generateDistributionGenerationRateValue(Object generationRateValue, TypesEnum type){
        Random rand = new Random();

        double rnd = rand.nextDouble();
        double maxProbability = 1;
        for(Distribution distribution: ((DistributionGenerationRate)generationRateValue).getDistributions()){
            if(rnd >= maxProbability - distribution.getProbability())
            { if(type.equals(BOOLEAN))
                return (T)distribution.getValue();
            else if(type.equals(INTEGER)){
                Integer value;
                value = (Integer) distribution.getMinValue() +  rand.nextInt(((Integer) distribution.getMaxValue() - (Integer) distribution.getMinValue()))+1;
                return castValue(value,type);
            }
            else{double value;
                value = (Double)distribution.getMinValue()+((Double)distribution.getMaxValue()-(Double)distribution.getMinValue())*rand.nextDouble();
                DecimalFormat df = new DecimalFormat("#.##");
                value  = Double.parseDouble(df.format(value));
                return castValue(value,type);}
            }
            else  maxProbability-=distribution.getProbability();
        }
        return  null;
    }


    public static <T>T generateNormalDistributionGenerationRateValue(Object generationRateValue, TypesEnum type){
        Random rand = new Random();
        if(type.equals(INTEGER)){
            int value;
            NormalDistributionGenerationRate normalDistributionGenerationRate = (NormalDistributionGenerationRate)generationRateValue;
            value = (int) (rand.nextGaussian() * normalDistributionGenerationRate.getDeviation().intValue() + normalDistributionGenerationRate.getMean().intValue());
            return castValue(value, type);
        }
        else{
            double value;
            NormalDistributionGenerationRate normalDistributionGenerationRate=(NormalDistributionGenerationRate)generationRateValue;
            value= rand.nextGaussian()*normalDistributionGenerationRate.getDeviation().doubleValue()+normalDistributionGenerationRate.getMean().doubleValue();
            DecimalFormat df = new DecimalFormat("#.##");
            value  = Double.parseDouble(df.format(value));
            return castValue(value,type);
        }

    }



    public static <T>T generateRandomGenerationRateValue(Object generationRateValue, TypesEnum type){
        Random rand = new Random();
        RandomGenerationRate randomGenerationRate = (RandomGenerationRate) generationRateValue;

        if(type.equals(INTEGER)){
            int value;
            value = (Integer) randomGenerationRate.getMinValue() +  rand.nextInt(((Integer) randomGenerationRate.getMaxValue() - (Integer) randomGenerationRate.getMinValue()))+1;
            return castValue(value,type);

        }
        else {
            double value;
            value = (Double) randomGenerationRate.getMinValue() + ((Double) randomGenerationRate.getMaxValue() - (Double) randomGenerationRate.getMinValue()) * rand.nextDouble();
            DecimalFormat df = new DecimalFormat("#.##");
            value  = Double.parseDouble(df.format(value));
            return castValue(value,type);
        }
    }


    public static <T>T generateConstantGenerationRateValue(Object generationRateValue,TypesEnum type) {
        ConstantGenerationRate constantGenerationRate=(ConstantGenerationRate)generationRateValue;
        return castValue(constantGenerationRate.getValue(),type);
    }

    public static Object generateGenerationRateValue(Object generationRateValue,TypesEnum type,GenerationRateEnum generationRateEnum){

        switch (generationRateEnum){
            case RANDOM:
                return  generateRandomGenerationRateValue(generationRateValue,type);
            case CONSTANT:
                return generateConstantGenerationRateValue(generationRateValue,type);
            case DISTRIBUTION:
                return generateDistributionGenerationRateValue(generationRateValue,type);
            case NORMAL_DISTRIBUTION:
                return  generateNormalDistributionGenerationRateValue(generationRateValue,type);
            default:return  null;
        }
    }

    public static void setGenerationRateToSensorPrototype(SensorPrototype sensorPrototype){
        String errorMessage = null;
        LinkedHashMap<String, Object> initialGenerationRate = (LinkedHashMap<String, Object>)sensorPrototype.getGenerationRate();

        if (initialGenerationRate.containsKey("normalDistribution")) {
            sensorPrototype.setGenerationRate(objectMapper.convertValue(initialGenerationRate.get("normalDistribution"), NormalDistributionGenerationRate.class));
            NormalDistributionGenerationRate updatedNornamlDistributionGenerationRate=GenerationRatesUtils.validateNormalDistributionGenerationRateValue(sensorPrototype.getGenerationRate(),TypesEnum.INTEGER);
            if(updatedNornamlDistributionGenerationRate == null)
                throw new RuntimeException("Value of generation rate of sensorPrototype {"+sensorPrototype.getSensorPrototypeName()+ "} is either null or don't match supported type: "+TypesEnum.INTEGER);
            sensorPrototype.setGenerationRate(updatedNornamlDistributionGenerationRate);
            sensorPrototype.setGenerationRateType(NORMAL_DISTRIBUTION);
        } else if (initialGenerationRate.containsKey("distributions")) {
            sensorPrototype.setGenerationRate(objectMapper.convertValue(sensorPrototype.getGenerationRate(), DistributionGenerationRate.class));
            DistributionGenerationRate updatedDistributionGenerationRate=GenerationRatesUtils.validateDistributionGenerationRateValue(sensorPrototype.getGenerationRate(),TypesEnum.INTEGER);
            if(updatedDistributionGenerationRate == null)
                throw new RuntimeException("Value of generation rate of sensorPrototype {"+sensorPrototype.getSensorPrototypeName()+ "} is either null or don't match supported type: "+TypesEnum.INTEGER);
            sensorPrototype.setGenerationRate(updatedDistributionGenerationRate);
            sensorPrototype.setGenerationRateType(DISTRIBUTION);
        } else if (initialGenerationRate.containsKey("constant")) {
            sensorPrototype.setGenerationRate(objectMapper.convertValue(initialGenerationRate.get("constant"), ConstantGenerationRate.class));
            if(!GenerationRatesUtils.validateConstantGenerationRateValue(TypesEnum.INTEGER,sensorPrototype.getGenerationRate()))
                throw new RuntimeException("Value of generation rate of sensorPrototype {"+sensorPrototype.getSensorPrototypeName()+ "} is either null or don't match supported type: "+TypesEnum.INTEGER);
            sensorPrototype.setGenerationRateType(CONSTANT);
        } else if (initialGenerationRate.containsKey("random")) {
            sensorPrototype.setGenerationRate(objectMapper.convertValue(initialGenerationRate.get("random"), RandomGenerationRate.class));
            RandomGenerationRate updatedRandomGenerarationRate=GenerationRatesUtils.validateRandomGenerationRateValue(sensorPrototype.getGenerationRate(),TypesEnum.INTEGER);
            if(updatedRandomGenerarationRate == null)
                throw new RuntimeException("Value of generation rate of sensorPrototype {"+sensorPrototype.getSensorPrototypeName()+ "} is either null or don't match supported type: "+TypesEnum.INTEGER);
            sensorPrototype.setGenerationRate(updatedRandomGenerarationRate);
            sensorPrototype.setGenerationRateType(RANDOM);
        } else {
            errorMessage = "Not supported generation rate type sensorPrototype " + sensorPrototype.getSensorPrototypeName();
            throw  new RuntimeException(errorMessage);
        }
    }

    public static GenerationRateWrapper processGenerationRate(Object generationRateObject) throws Exception{
        GenerationRateWrapper generationRateWrapper = new GenerationRateWrapper();
        String errorMessage = null;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LinkedHashMap<String, Object> initialGenerationRate = (LinkedHashMap<String, Object>) generationRateObject;
            if(initialGenerationRate.get("value") != null)
                initialGenerationRate = (LinkedHashMap<String, Object>)initialGenerationRate.get("value");
            if (initialGenerationRate.containsKey("normalDistribution")) {
                generationRateWrapper.setGenerationRate(objectMapper.convertValue(initialGenerationRate.get("normalDistribution"), NormalDistributionGenerationRate.class));
                generationRateWrapper.setGenerationRateEnum(NORMAL_DISTRIBUTION);
            } else if (initialGenerationRate.containsKey("distributions")) {
                generationRateWrapper.setGenerationRate(objectMapper.convertValue(initialGenerationRate, DistributionGenerationRate.class));
                generationRateWrapper.setGenerationRateEnum(DISTRIBUTION);
            } else if (initialGenerationRate.containsKey("constant")) {
                generationRateWrapper.setGenerationRate(objectMapper.convertValue(initialGenerationRate.get("constant"), ConstantGenerationRate.class));
                generationRateWrapper.setGenerationRateEnum(CONSTANT);
            } else if (initialGenerationRate.containsKey("random")) {
                generationRateWrapper.setGenerationRate(objectMapper.convertValue(initialGenerationRate.get("random"), RandomGenerationRate.class));
                generationRateWrapper.setGenerationRateEnum(RANDOM);
            } else {
                errorMessage = "Not supported Generation rate type";
                log.error(errorMessage);
            }
        } catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            throw exception;
        }
        if (errorMessage != null)
            throw new Exception(errorMessage);
        return generationRateWrapper;
    }

}
