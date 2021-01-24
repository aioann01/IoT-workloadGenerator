package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;

import java.text.DecimalFormat;
import java.util.Random;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.INTEGER;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.castValue;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.validateNumberValueType;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.DECIMAL_FORMAT_PATTERN;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NormalDistributionGenerationRate extends GenerationRate{

    private Number mean;

    private Number deviation;

    public Number getMean() {
        return mean;
    }

    public void setMean(Number mean) {
        this.mean = mean;
    }

    public Number getDeviation() {
        return deviation;
    }

    public void setDeviation(Number deviation) {
        this.deviation = deviation;
    }

    @Override
    public String toString() {
        return "\"normalDistribution\":{" +
                "\"mean\":" + mean +
                ", \"deviation\":" + deviation +
                '}';
    }

    @Override
    public Object generateValue(TypesEnum type) {
        Random rand = new Random();
        if(type.equals(INTEGER)){
            Integer value;
            value = (int) (rand.nextGaussian() * deviation.intValue() + mean.intValue());
            return castValue(value, type);
        }
        else{
            Double value;
            value = rand.nextGaussian() * deviation.doubleValue() + mean.doubleValue();
            DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
            value  = Double.parseDouble(df.format(value));
            return castValue(value, type);
        }
    }

    @Override
    public void processAndValidate(TypesEnum type) throws ValidationException {
        Number updatedMean = validateNumberValueType(type, mean);
        Number updatedDeviation = validateNumberValueType(type, deviation);
        if(updatedMean == null || updatedDeviation == null)
            throw new ValidationException("toDo");
        else{
            mean = updatedMean;
            deviation = updatedDeviation;
        }
    }
}
