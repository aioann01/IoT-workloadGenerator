package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import java.util.List;

public interface IMockSensorPrototypeRepository {


    MockSensorPrototype getMockSensorPrototypeByMockSensorPrototypeName(String mockSensorPrototypeName);

    List<MockSensorPrototype> getAllMockSensorPrototypes();

    boolean deleteMockSensorPrototype(String mockSensorPrototypeName);

    void addMockSensorPrototype(MockSensorPrototype mockSensorPrototype);

    void addAllMockSensorPrototypes(List<MockSensorPrototype> mockSensorPrototypes);

    void updateMockSensorPrototype(String mockSensorPrototypeName, MockSensorPrototype updatedMockSensorPrototype);

    void deleteAll();


}
