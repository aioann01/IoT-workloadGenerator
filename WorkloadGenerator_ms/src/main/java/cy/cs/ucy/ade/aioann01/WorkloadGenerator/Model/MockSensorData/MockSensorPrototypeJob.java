package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MockSensorPrototypeJob {
    private String mockSensorPrototypeName;

    private MockSensorPrototype mockSensorPrototype;

    private AtomicInteger sensorsNumber;

    private AtomicInteger counter;


    public MockSensorPrototypeJob(MockSensorPrototype mockSensorPrototype){
        this.mockSensorPrototype = mockSensorPrototype;
        this.mockSensorPrototypeName = mockSensorPrototype.getSensorPrototypeName();
        this.counter = new AtomicInteger(0);
        this.sensorsNumber = new AtomicInteger(0);

    }

    public MockSensorPrototype getMockSensorPrototype() {
        return mockSensorPrototype;
    }

    public void setMockSensorPrototype(MockSensorPrototype mockSensorPrototype) {
        this.mockSensorPrototype = mockSensorPrototype;
    }

    public String getMockSensorPrototypeName() {
        return mockSensorPrototypeName;
    }

    public void setMockSensorPrototypeName(String mockSensorPrototypeName) {
        this.mockSensorPrototypeName = mockSensorPrototypeName;
    }

    public AtomicInteger getSensorsNumber() {
        return sensorsNumber;
    }

    public void setSensorsNumber(AtomicInteger sensorsNumber) {
        this.sensorsNumber = sensorsNumber;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }
//    public int getCounter() {
//        return counter;
//    }
//
//    public void setCounter(int counter) {
//        this.counter = counter;
//    }
//
//    public int getSensorsNumber() {
//        return sensorsNumber;
//    }
//
//    public void setSensorsNumber(int sensorsNumber) {
//        this.sensorsNumber = sensorsNumber;
//    }

    public  List<MockSensor> createMockSensors() {
        List<MockSensor> mockSensors = new LinkedList<>();
        for (int i = 0; i < mockSensorPrototype.getSensorsQuantity(); ++i) {
            mockSensors.add(new MockSensor(mockSensorPrototype, counter.getAndAdd(1)));
            sensorsNumber.getAndIncrement();
        }
        return mockSensors;
    }
}
