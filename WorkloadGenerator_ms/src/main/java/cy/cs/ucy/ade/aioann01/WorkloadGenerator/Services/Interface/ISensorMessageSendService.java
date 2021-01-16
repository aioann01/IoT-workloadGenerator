package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import org.codehaus.jettison.json.JSONObject;


public interface ISensorMessageSendService {

     void validateAndProcessConfigs(JSONObject protocolConfigs) throws ValidationException, Exception;

     void initializeConnections() throws Exception;

     void sendMessage(String sensorId, String message, SensorMessageEnum contentType) throws Exception;

     void terminate() throws Exception;


}
