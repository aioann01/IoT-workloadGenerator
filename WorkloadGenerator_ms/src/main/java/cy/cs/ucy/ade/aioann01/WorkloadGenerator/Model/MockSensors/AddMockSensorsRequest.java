package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors;

public class AddMockSensorsRequest {

    private Integer quantity;

    private String mockSensorPrototypeName;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMockSensorPrototypeName() {
        return mockSensorPrototypeName;
    }

    public void setMockSensorPrototypeName(String mockSensorPrototypeName) {
        this.mockSensorPrototypeName = mockSensorPrototypeName;
    }
}
