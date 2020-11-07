package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MockSensor {
    private String id;

    private MockSensorPrototype mockSensorPrototype;

    public MockSensor(){}

    public MockSensor(MockSensorPrototype mockSensorPrototype, int counter){
        this.mockSensorPrototype=mockSensorPrototype;
        this.id=mockSensorPrototype.getSensorPrototypeName()+"_"+counter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MockSensorPrototype getMockSensorPrototype() {
        return mockSensorPrototype;
    }

    public void setMockSensorPrototype(MockSensorPrototype mockSensorPrototype) {
        this.mockSensorPrototype = mockSensorPrototype;
    }
}
