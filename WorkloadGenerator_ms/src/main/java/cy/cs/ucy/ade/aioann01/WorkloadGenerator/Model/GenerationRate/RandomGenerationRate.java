package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;

import java.text.DecimalFormat;
import java.util.Random;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.DOUBLE;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.INTEGER;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.castValue;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.validateNumberValueType;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.DECIMAL_FORMAT_PATTERN;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RandomGenerationRate<T> extends GenerationRate{

    private Number minValue;

    private Number maxValue;

    public RandomGenerationRate(Number minValue, Number maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    public RandomGenerationRate(){}

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue = minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return "\"RandomGenerationRate\":{" +
                "\"minValue\":" + minValue +
                ", \"maxValue\":" + maxValue +
                '}';
    }

    @Override
    public Object generateValue(TypesEnum type) {
        Random rand = new Random();
        if(type.equals(INTEGER)){
            Integer value;
            value = minValue.intValue() +  rand.nextInt(maxValue.intValue() - minValue.intValue()) + 1;
            return castValue(value, type);
        }
        else {
            Double value;
            value = (Double) minValue + (maxValue.doubleValue() - minValue.doubleValue()) * rand.nextDouble();
            DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
            value  = Double.parseDouble(df.format(value));
            return castValue(value, type);
        }
    }

    @Override
    public void processAndValidate(TypesEnum type) throws ValidationException {
        if(type.equals(DOUBLE) || type.equals(INTEGER)){
            Number updatedMinValue = validateNumberValueType(type, minValue);
            Number updatedMaxValue = validateNumberValueType(type, maxValue);
            if(updatedMinValue == null || updatedMaxValue == null)
                throw new ValidationException("toDo");
            else{
                maxValue = updatedMaxValue;
                minValue = updatedMinValue;
            }
        }
        else
            throw new ValidationException("toDo");
    }
}