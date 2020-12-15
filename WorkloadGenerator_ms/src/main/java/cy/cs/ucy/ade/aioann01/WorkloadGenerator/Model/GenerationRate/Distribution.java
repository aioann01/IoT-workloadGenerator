package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Distribution<T> {

    private Number minValue;

    private Number maxValue;

    private Object value;

    private Double probability;

    public Distribution(){}

    public Distribution(Boolean booleanValue, Double probability){
        this.probability = probability;
        this.value = booleanValue;
    }

    public Distribution(Number minValue, Number maxValue, Double probability){
        this.probability = probability;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

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

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
