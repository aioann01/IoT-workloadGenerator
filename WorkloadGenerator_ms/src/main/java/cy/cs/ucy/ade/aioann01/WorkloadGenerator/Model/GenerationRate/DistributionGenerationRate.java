package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.INTEGER;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.validateNumberValueType;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.DECIMAL_FORMAT_PATTERN;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistributionGenerationRate extends GenerationRate{

    List<Distribution> distributions;

    public DistributionGenerationRate(){}

    public  DistributionGenerationRate(List<Distribution> distributions){
        this.distributions = distributions;
    }

    public List<Distribution> getDistributions() {
        return distributions;
    }

    public void setDistributions(List<Distribution> distributions) {
        this.distributions = distributions;
    }

    @Override
    public Object generateValue(TypesEnum type) {
        Random rand = new Random();
        double rnd = rand.nextDouble();
        double maxProbability = 1;
        for(Distribution distribution : distributions){
            if(rnd >= maxProbability - distribution.getProbability()){
                if(distribution.getValue() != null){
                    // For Boolean type
                    return /*(T)*/distribution.getValue();
                }
                else {//For Number type
                    if(type.equals(INTEGER)){
                        Integer value;
                        value = distribution.getMinValue().intValue() +  rand.nextInt(( distribution.getMaxValue().intValue() -  distribution.getMinValue().intValue())) +1;
                        return castValue(value, type);
                    }
                    else{
                        Double value;
                        value = distribution.getMinValue().doubleValue() + (distribution.getMaxValue().doubleValue() -distribution.getMinValue().doubleValue()) * rand.nextDouble();
                        DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
                        value  = Double.parseDouble(df.format(value));
                        return castValue(value, type);
                    }
                }
            }
            else maxProbability -= distribution.getProbability();
        }
        return null;
    }

    @Override
    public void processAndValidate(TypesEnum type) throws ValidationException {
        double probabilitySum = 0;
        for(Distribution distribution: distributions) {
            if(distribution.getValue() != null){
                if(distribution.getValue() == null || !isValueOfType(type, distribution.getValue()))
                    throw new ValidationException("Value of distribution of DistributionGenerationRate is not the same as the type");
            }
            else{
                Number updatedMinValue = validateNumberValueType(type, distribution.getMinValue());
                Number updatedMaxValue = validateNumberValueType(type, distribution.getMaxValue());
                if(updatedMaxValue == null || updatedMinValue == null || distribution.getProbability() == null || !(distribution.getProbability() instanceof Double))
                    throw new ValidationException("Could not process DistributionGenerationRate. Distributions Min and Max values must be Numbers");
                else{
                    distribution.setMinValue(updatedMinValue);
                    distribution.setMaxValue(updatedMaxValue);
                }
            }
            probabilitySum += distribution.getProbability();}
        if(probabilitySum < 1.0 || probabilitySum > 1.0)
            throw new ValidationException("Sum of Probabilities must be equal to 1 for distribution DistributionGenerationRate");
    }
}
