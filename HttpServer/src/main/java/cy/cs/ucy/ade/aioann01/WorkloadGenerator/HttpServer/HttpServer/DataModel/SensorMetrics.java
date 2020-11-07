package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;

import java.util.Date;

public class SensorMetrics {

    private int sensorsMessagesCount;

    private double sensorsMessagesDelaySum;

    private Date previousSensorMessageDate;

    public SensorMetrics() {
        this.sensorsMessagesCount = 0;
        this.sensorsMessagesDelaySum = 0.0;
    }

    public int getSensorsMessagesCount() {
        return sensorsMessagesCount;
    }

    public void setSensorsMessagesCount(int sensorsMessagesCount) {
        this.sensorsMessagesCount = sensorsMessagesCount;
    }

    public double getSensorsMessagesDelaySum() {
        return sensorsMessagesDelaySum;
    }

    public void setSensorsMessagesDelaySum(double sensorsMessagesDelaySum) {
        this.sensorsMessagesDelaySum = sensorsMessagesDelaySum;
    }

    public Date getPreviousSensorMessageDate() {
        return previousSensorMessageDate;
    }

    public void setPreviousSensorMessageDate(Date previousSensorMessageDate) {
        this.previousSensorMessageDate = previousSensorMessageDate;
    }
}
