package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData;

public class AddMockSensorsRequest {

    private int quantity;

    private String mockSensorPrototypeName;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMockSensorPrototypeName() {
        return mockSensorPrototypeName;
    }

    public void setMockSensorPrototypeName(String mockSensorPrototypeName) {
        this.mockSensorPrototypeName = mockSensorPrototypeName;
    }
}
