package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Server;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

public interface ISensorMessageSendService {

     void initializeServiceReceiverConfigurations(List<Server> httpServers, HashMap<String,String> configs) throws Exception;

     void sendMessage(String sensorId, String message, SensorMessageEnum contentType) throws Exception;

     void terminate() throws Exception;


}
