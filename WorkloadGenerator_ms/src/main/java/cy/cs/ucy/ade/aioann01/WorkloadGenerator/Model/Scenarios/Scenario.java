package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;

import java.util.List;

public class Scenario{

    @JsonProperty("sensorId")
    private String sensorId;

    @JsonProperty("scenarioDelay")
    private Integer scenarioDelay;

    @JsonProperty("scenarioDuration")
    private Integer scenarioDuration;

    @JsonProperty("scenarioFieldValueInfoList")
    private List<ScenarioFieldValueInfo> scenarioFieldValueInfoList;

    @JsonProperty("scenarioName")
    private String scenarioName;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Integer getScenarioDelay() {
        return scenarioDelay;
    }

    public void setScenarioDelay(Integer scenarioDelay) {
        this.scenarioDelay = scenarioDelay;
    }

    public List<ScenarioFieldValueInfo> getScenarioFieldValueInfoList() {
        return scenarioFieldValueInfoList;
    }

    public void setScenarioFieldValueInfoList(List<ScenarioFieldValueInfo> scenarioFieldValueInfoList) {
        this.scenarioFieldValueInfoList = scenarioFieldValueInfoList;
    }

    public Integer getScenarioDuration() {
        return scenarioDuration;
    }

    public void setScenarioDuration(Integer scenarioDuration) {
        this.scenarioDuration = scenarioDuration;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }


}
