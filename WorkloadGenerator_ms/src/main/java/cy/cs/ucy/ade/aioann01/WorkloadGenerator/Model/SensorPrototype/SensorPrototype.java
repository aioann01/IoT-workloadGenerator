package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorMessagePrototype;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorPrototype<T> {
    private String sensorPrototypeName;

    private GenerationRateEnum generationRateType;

    private T generationRate;

    private SensorMessagePrototype messagePrototype;

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

    public String getSensorPrototypeName() {
        return sensorPrototypeName;
    }

    public void setSensorPrototypeName(String sensorPrototypeName) {
        this.sensorPrototypeName = sensorPrototypeName;
    }
}
