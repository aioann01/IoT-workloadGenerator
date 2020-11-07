package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData.MockSensor;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@Repository
public class MockSensorRepository implements IMockSensorRepository{

    List<MockSensor> mockSensors =new ArrayList<>();


    @Override
    public MockSensor getMockSensorByMockSensorId(String mockSensorId) {
        Optional<MockSensor> mockSensorOptional=   mockSensors
                .stream()
                .filter(mcokSensor -> mcokSensor.getId().equals(mockSensorId))
                .findFirst();

        if(mockSensorOptional.isPresent())
            return mockSensorOptional.get();
        return null;
    }

    @Override
    public List<MockSensor> getAllMockSensors() {
        return mockSensors;
    }

    @Override
    public void addMockSensor(MockSensor mockSensor) {
        this.mockSensors.add(mockSensor);
    }

    @Override
    public void addAllMockSensors(List<MockSensor> mockSensors) {
        this.mockSensors.addAll(mockSensors);
    }

    @Override
    public boolean deleteMockSensor(String mockSensorId) {
        ListIterator<MockSensor> mockSensorIterator= mockSensors.listIterator();
        while(mockSensorIterator.hasNext()){
            if(mockSensorIterator.next().getId().equals(mockSensorId)){
                mockSensorIterator.remove();
                return true;
            }
        }
        return false;
    }



    public void deleteMockSensorsOfMockSensorPrototype(String mockSensorPrototypeName){
        ListIterator<MockSensor> mockSensorsIterator= mockSensors.listIterator();
        while(mockSensorsIterator.hasNext()){
            MockSensor mockSensor = mockSensorsIterator.next();
            if(mockSensor.getMockSensorPrototype().getSensorPrototypeName().equals(mockSensorPrototypeName))
                mockSensorsIterator.remove();}
    }

    @Override
    public void deleteAll(){
        ListIterator<MockSensor> mockSensorListIterator= mockSensors.listIterator();
        while(mockSensorListIterator.hasNext()){
            mockSensorListIterator.next();
            mockSensorListIterator.remove();
        }
    }

}
