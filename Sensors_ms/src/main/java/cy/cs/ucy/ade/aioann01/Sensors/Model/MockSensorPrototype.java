package cy.cs.ucy.ade.aioann01.Sensors.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Enums.GenerationRateEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MockSensorPrototype<T> {

    private String sensorPrototypeName;

    private int sensorsQuantity;

    private String description;

    private GenerationRateEnum generationRateType;

    private T generationRate;

    private SensorMessagePrototype messagePrototype;

    public MockSensorPrototype(){}

    public String getSensorPrototypeName() {
        return sensorPrototypeName;
    }

    public void setSensorPrototypeName(String sensorPrototypeName) {
        this.sensorPrototypeName = sensorPrototypeName;
    }

    public int getSensorsQuantity() {
        return sensorsQuantity;
    }

    public void setSensorsQuantity(int sensorsQuantity) {
        this.sensorsQuantity = sensorsQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GenerationRateEnum getGenerationRateType() {
        return generationRateType;
    }

    public void setGenerationRateType(GenerationRateEnum generationRateType) {
        this.generationRateType = generationRateType;
    }

    public T getGenerationRate() {
        return generationRate;
    }

    public void setGenerationRate(T generationRate) {
        this.generationRate = generationRate;
    }

    public SensorMessagePrototype getMessagePrototype() {
        return messagePrototype;
    }

    public void setMessagePrototype(SensorMessagePrototype messagePrototype) {
        this.messagePrototype = messagePrototype;
    }
}
