package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http;

public class SensorMessage {

    private String message;

    private String sensorId;

    public SensorMessage() {}

    public SensorMessage(String message, String sensorId) {
        this.message = message;
        this.sensorId = sensorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public String toString() {
        return "SensorMessage : {" +
                "message='" + message + '\'' +
                ", sensorId='" + sensorId + '\'' +
                '}';
    }
}
