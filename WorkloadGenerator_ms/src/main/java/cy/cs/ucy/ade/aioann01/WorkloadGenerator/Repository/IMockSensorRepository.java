package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.MockSensor;
import java.util.List;

public interface IMockSensorRepository {


    public MockSensor getMockSensorByMockSensorId(String mockSensorId);

    public List<MockSensor> getAllMockSensors();

    public void addMockSensor(MockSensor mockSensor);

    public void addAllMockSensors(List<MockSensor> mockSensors);

    public boolean deleteMockSensor(String mockSensorId);

    public void deleteMockSensorsOfMockSensorPrototype(String mockSensorPrototypeName);

    public void deleteAll();
}
