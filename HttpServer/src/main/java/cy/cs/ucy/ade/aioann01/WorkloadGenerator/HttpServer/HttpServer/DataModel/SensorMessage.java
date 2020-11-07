package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;
import java.util.Date;

public class SensorMessage {

    private String message;

    private String sensorId;

    private Date dateSent;

    public SensorMessage() {}

    public SensorMessage(String message, String sensorId, Date dateSent) {
        this.message = message;
        this.sensorId = sensorId;
        this.dateSent = dateSent;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
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
        return "SensorMessage{" +
                "message='" + message + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", dateSent=" + dateSent +
                '}';
    }
}
