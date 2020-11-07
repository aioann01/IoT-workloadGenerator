package cy.cs.ucy.ade.aioann01.Sensors.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Enums.SensorMessageEnum;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorMessagePrototype {
    private SensorMessageEnum sensorMessageType;

    private List<FieldPrototype> fieldsPrototypes;

    public SensorMessagePrototype(){}

    public SensorMessagePrototype(SensorMessageEnum sensorMessageType, List<FieldPrototype> fieldsPrototypes) {
        this.sensorMessageType = sensorMessageType;
        this.fieldsPrototypes = fieldsPrototypes;
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
