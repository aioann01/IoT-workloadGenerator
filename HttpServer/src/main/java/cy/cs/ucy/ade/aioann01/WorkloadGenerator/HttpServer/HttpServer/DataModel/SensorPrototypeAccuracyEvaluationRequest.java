package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorPrototypeAccuracyEvaluationRequest {
    @JsonProperty("sensorPrototypeName")
    private String sensorPrototypeName;

    @JsonProperty("messageIntervalTime")
    private Integer messageIntervalTime;

    public String getSensorPrototypeName() {
        return sensorPrototypeName;
    }

    public void setSensorPrototypeName(String sensorPrototypeName) {
        this.sensorPrototypeName = sensorPrototypeName;
    }

    public Integer getMessageIntervalTime() {
        return messageIntervalTime;
    }

    public void setMessageIntervalTime(Integer messageIntervalTime) {
        this.messageIntervalTime = messageIntervalTime;
    }
}
