package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;

import java.io.Serializable;
import java.util.List;

public class GetMockSensorPrototypesResponse implements Serializable {

    private int totalCount;

    private List<MockSensorPrototype> mockSensorPrototypes;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<MockSensorPrototype> getMockSensorPrototypes() {
        return mockSensorPrototypes;
    }

    public void setMockSensorPrototypes(List<MockSensorPrototype> mockSensorPrototypes) {
        this.mockSensorPrototypes = mockSensorPrototypes;
    }
}
