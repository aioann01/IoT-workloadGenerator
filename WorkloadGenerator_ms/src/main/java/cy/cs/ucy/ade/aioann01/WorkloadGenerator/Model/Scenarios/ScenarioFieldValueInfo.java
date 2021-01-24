package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScenarioFieldValueInfo {

    @JsonProperty("sensorFieldScenarioName")
    private String sensorFieldScenarioName;

    @JsonProperty("sensorFieldScenarioGenerationRate")
    private Object sensorFieldScenarioGenerationRate;


    public String getSensorFieldScenarioName() {
        return sensorFieldScenarioName;
    }

    public void setSensorFieldScenarioName(String sensorFieldScenarioName) {
        this.sensorFieldScenarioName = sensorFieldScenarioName;
    }

    public Object getSensorFieldScenarioGenerationRate() {
        return sensorFieldScenarioGenerationRate;
    }

    public void setSensorFieldScenarioGenerationRate(Object sensorFieldScenarioGenerationRate) {
        this.sensorFieldScenarioGenerationRate = sensorFieldScenarioGenerationRate;
    }

}
