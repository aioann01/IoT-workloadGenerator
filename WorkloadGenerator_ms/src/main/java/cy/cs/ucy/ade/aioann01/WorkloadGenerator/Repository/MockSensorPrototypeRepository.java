package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@Repository
public class MockSensorPrototypeRepository implements IMockSensorPrototypeRepository {


    List<MockSensorPrototype> mockSensorPrototypes = new ArrayList<>();


    @Override
    public MockSensorPrototype getMockSensorPrototypeByMockSensorPrototypeName(String mockSensorPrototypeName) {
        Optional<MockSensorPrototype> mockSensorPrototypeOptional =  mockSensorPrototypes
                .stream()
                .filter(mockSensorPrototype -> mockSensorPrototype.getSensorPrototypeName().equals(mockSensorPrototypeName))
                .findFirst();
        if(mockSensorPrototypeOptional.isPresent())
            return mockSensorPrototypeOptional.get();
        else
            return null;
    }


    @Override
    public List<MockSensorPrototype> getAllMockSensorPrototypes() {
        return mockSensorPrototypes;
    }


    @Override
    public boolean deleteMockSensorPrototype(String mockSensorPrototypeName){
        ListIterator<MockSensorPrototype> mockSensorPrototypeListIterator = mockSensorPrototypes.listIterator();
        while(mockSensorPrototypeListIterator.hasNext())
            if(mockSensorPrototypeListIterator.next().getSensorPrototypeName().equals(mockSensorPrototypeName)){
                mockSensorPrototypeListIterator.remove();
                return true;
            }
        return false;
    }


    @Override
    public void addMockSensorPrototype(MockSensorPrototype mockSensorPrototypes) {
        this.mockSensorPrototypes.add(mockSensorPrototypes);
    }


    @Override
    public void addAllMockSensorPrototypes(List<MockSensorPrototype> mockSensorPrototypes) {
        this.mockSensorPrototypes.addAll(mockSensorPrototypes);
    }


    @Override
    public void deleteAll(){
        ListIterator<MockSensorPrototype> mockSensorPrototypeListIterator = mockSensorPrototypes.listIterator();
        while(mockSensorPrototypeListIterator.hasNext()){
            mockSensorPrototypeListIterator.next();
            mockSensorPrototypeListIterator.remove();
        }
    }


    @Override
    public void updateMockSensorPrototype(String mockSensorPrototypeName, MockSensorPrototype updatedMockSensorPrototype){
        for(int i = 0; i < mockSensorPrototypes.size(); ++ i)
            if(mockSensorPrototypes.get(i).getSensorPrototypeName().equals(mockSensorPrototypeName)){
                mockSensorPrototypes.set(i, updatedMockSensorPrototype);
                break;
            }
    }
}
