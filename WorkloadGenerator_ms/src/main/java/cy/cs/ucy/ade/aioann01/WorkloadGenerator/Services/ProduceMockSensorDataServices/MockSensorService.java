package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.GetSensorsResponse;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.MockSensor;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository.MockSensorRepository;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;

@Service
public class MockSensorService {

    @Autowired
    MockSensorRepository mockSensorRepository;


    public void addAllMockSensors(List<MockSensor> mockSensors){
        mockSensorRepository.addAllMockSensors(mockSensors);
    }


    public List<MockSensor> getAllMockSensors(){
        return mockSensorRepository.getAllMockSensors();
    }


    public void findMockSensorById(Exchange exchange, String mockSensorId){
        MockSensor mockSensor = mockSensorRepository.getMockSensorByMockSensorId(mockSensorId);
        if(mockSensor != null)
            exchange.setBody(mockSensor);
        else{
            CommonUtils.setNotFoundErrorOnExchange(exchange, "MockSensor with id: "+mockSensorId +" not found", VALIDATION_ERROR);
        }
    }


    public void retrieveAllMockSensors(Exchange exchange){
        GetSensorsResponse response = new GetSensorsResponse();
        response.setMockSensors(mockSensorRepository.getAllMockSensors());
        response.setTotalCount(response.getMockSensors().size());
        exchange.setBody(response);
    }


    public void addMockSensor(MockSensor mockSensor){
        mockSensorRepository.addMockSensor(mockSensor);
    }


    public void deleteMockSensor(String mockSensorId){
        mockSensorRepository.deleteMockSensor(mockSensorId);
    }


    public void deleteMockSensorsOfMockSensorPrototype(String mockSensorPrototypeName){
        mockSensorRepository.deleteMockSensorsOfMockSensorPrototype(mockSensorPrototypeName);
    }


    public void deleteAll(){
        mockSensorRepository.deleteAll();
    }


}
