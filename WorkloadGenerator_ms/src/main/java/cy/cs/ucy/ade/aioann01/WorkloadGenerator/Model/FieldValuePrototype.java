package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldValuePrototype<T> implements Serializable {
    private static final long serialVersionUID = 4L;

    private String type;
    private T value;
    private String unit;
    private T valueRange;

    @JsonCreator
    public FieldValuePrototype(@JsonProperty("type") String type, @JsonProperty("value")T value, @JsonProperty("unit")String unit, @JsonProperty("valueRange")T valueRange) {
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.valueRange=valueRange;
    }
    //public FieldValue(){}


    public T getValueRange() {
        return valueRange;
    }

    public void setValueRange(T valueRange) {
        this.valueRange = valueRange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
