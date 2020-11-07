package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorMessagePrototype {

    private String type;

    private SensorMessageEnum sensorMessageType;

    private List<FieldPrototype> fieldsPrototypes;

    public SensorMessagePrototype(){}

    public SensorMessagePrototype(SensorMessageEnum sensorMessageEnum, List<FieldPrototype> fieldsPrototypes) {
        this.sensorMessageType = sensorMessageEnum;
        this.fieldsPrototypes = fieldsPrototypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SensorMessageEnum getSensorMessageType() {
        return sensorMessageType;
    }

    public void setSensorMessageType(SensorMessageEnum sensorMessageType) {
        this.sensorMessageType = sensorMessageType;
    }

    public List<FieldPrototype> getFieldsPrototypes() {
        return fieldsPrototypes;
    }

    public void setFieldsPrototypes(List<FieldPrototype> fieldsPrototypes) {
        this.fieldsPrototypes = fieldsPrototypes;
    }
}
