package cy.cs.ucy.ade.aioann01.Sensors.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.Sensors.Utils.Utils;

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
    public Object generateMessage(TypesEnum type) {
        String toClass= Utils.classType(type);
//Class.forName(toClass).
        return null;
    }
}