package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors;

import java.io.Serializable;
import java.util.List;

public class GetSensorsResponse implements Serializable {

    private int totalCount;

    private List<MockSensor> mockSensors;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<MockSensor> getMockSensors() {
        return mockSensors;
    }

    public void setMockSensors(List<MockSensor> mockSensors) {
        this.mockSensors = mockSensors;
    }
}
