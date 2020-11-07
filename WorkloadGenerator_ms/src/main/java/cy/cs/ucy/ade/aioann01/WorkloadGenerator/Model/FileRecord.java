package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

import java.util.Date;

public class FileRecord {

    private String MockSensorPrototypeName;

    private String sensorId;

    private String timestamp;

    private Object message;

    private String date;

    public String getMockSensorPrototypeName() {
        return MockSensorPrototypeName;
    }

    public void setMockSensorPrototypeName(String mockSensorPrototypeName) {
        MockSensorPrototypeName = mockSensorPrototypeName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FileRecord{" +
                "sensorId='" + sensorId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
