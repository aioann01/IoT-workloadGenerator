package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.GetMockSensorPrototypesResponse;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository.MockSensorPrototypeRepository;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;

@Service
public class MockSensorPrototypeService{

    @Autowired
    public MockSensorPrototypeRepository mockSensorPrototypeRepository;

    public void findMockSensorPrototypeByName(Exchange exchange, String mockSensorPrototypeName){
        MockSensorPrototype mockSensorPrototype = mockSensorPrototypeRepository.getMockSensorPrototypeByMockSensorPrototypeName(mockSensorPrototypeName);
        if(mockSensorPrototype != null)
            exchange.setBody(mockSensorPrototype);
        else{
            Utils.setNotFoundErrorOnExchange(exchange, "MockSensorPrototype with name: "+mockSensorPrototypeName +" not found", VALIDATION_ERROR);
        }

    }


    public MockSensorPrototype retrieveMockSensorPrototypeByName(String mockSensorPrototypeName){
        return mockSensorPrototypeRepository.getMockSensorPrototypeByMockSensorPrototypeName(mockSensorPrototypeName);
    }


    public void retrieveAllMockSensorPrototypes(Exchange exchange) {
       GetMockSensorPrototypesResponse response = new GetMockSensorPrototypesResponse();
       response.setMockSensorPrototypes(mockSensorPrototypeRepository.getAllMockSensorPrototypes());
       response.setTotalCount(response.getMockSensorPrototypes().size());
       exchange.setBody(response);
    }


    public void addMockSensorPrototype(MockSensorPrototype mockSensorPrototype) {
        mockSensorPrototypeRepository.addMockSensorPrototype(mockSensorPrototype);
    }


    public void addAllMockSensorPrototypes(List<MockSensorPrototype> mockSensorPrototypes) {
        mockSensorPrototypeRepository.addAllMockSensorPrototypes(mockSensorPrototypes);
    }


    public void deleteMockSensorPrototype(String mockSensorPrototypeName) {
        mockSensorPrototypeRepository.deleteMockSensorPrototype(mockSensorPrototypeName);
    }


    public void deleteAll(){
        mockSensorPrototypeRepository.deleteAll();
    }


    public void editMockSensorsNumberForMockSensorPrototype(int mockSensorCount, String mockSensorPrototypeName){
        MockSensorPrototype mockSensorPrototype = mockSensorPrototypeRepository.getMockSensorPrototypeByMockSensorPrototypeName(mockSensorPrototypeName);
        if(mockSensorPrototype != null) {
            mockSensorPrototype.setSensorsQuantity(mockSensorCount);
            mockSensorPrototypeRepository.updateMockSensorPrototype(mockSensorPrototypeName, mockSensorPrototype);
        }
    }

}
