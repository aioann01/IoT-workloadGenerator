package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.SensorPrototype;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MockSensorPrototype extends SensorPrototype {

    private int sensorsQuantity;

    private String description;

    private String outputFile;

    private Boolean evaluateFieldGenerationRate;

    public Boolean getEvaluateFieldGenerationRate() {
        return evaluateFieldGenerationRate;
    }

    public void setEvaluateFieldGenerationRate(Boolean evaluateFieldGenerationRate) {
        this.evaluateFieldGenerationRate = evaluateFieldGenerationRate;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
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
}
