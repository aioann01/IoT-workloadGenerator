package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;

import java.util.Date;

public class SensorMessageDetailsProcessed {

    private String sensorId;

    private Object message;

    private Date date;


    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
