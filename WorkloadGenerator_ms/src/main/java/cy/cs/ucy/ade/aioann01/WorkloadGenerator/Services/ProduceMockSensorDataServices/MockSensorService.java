package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData.GetSensorsResponse;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData.MockSensor;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Repository.MockSensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.HTTP_NOT_FOUND;

@Service
public class MockSensorService {

    @Autowired
    MockSensorRepository mockSensorRepository;

    private static final Logger log= LoggerFactory.getLogger(MockSensorService.class);

    public void addAllMockSensors(List<MockSensor> mockSensors){
        mockSensorRepository.addAllMockSensors(mockSensors);
    }

    public List<MockSensor> getAllMockSensors(){
        return  mockSensorRepository.getAllMockSensors();
    }

    public void  findMockSensorById(Exchange exchange,String mockSensorId){
        MockSensor mockSensor = mockSensorRepository.getMockSensorByMockSensorId(mockSensorId);
        if(mockSensor !=null)
            exchange.setBody(mockSensor);
        else{
            exchange.setProperty(ERROR_MESSAGE_TYPE,VALIDATION_ERROR);
            exchange.setProperty(ERROR_MESSAGE,"MockSensor with id: "+mockSensorId +" not found");
            exchange.setHttpStatus(HTTP_NOT_FOUND);}
    }


    public void  retrieveAllMockSensors(Exchange exchange){
        GetSensorsResponse response=new GetSensorsResponse();
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

    public void deleteMockSensorsOfMockSensorPorotype(String mockSensorPrototypeName){
        mockSensorRepository.deleteMockSensorsOfMockSensorPrototype(mockSensorPrototypeName);
    }

    public void deleteAll(){
        mockSensorRepository.deleteAll();
    }


}
