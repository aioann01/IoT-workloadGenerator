package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.WorkloadGenerator;
import org.codehaus.jettison.json.JSONObject;

public interface ISensorDataProducerService {


     void initiate(Exchange exchange, ISensorMessageSendService sensorMessageSendService) throws Exception;

     void pause(Exchange exchange)throws Exception;

     void resume(Exchange exchange)throws Exception;

     void terminate(Exchange exchange)throws Exception;

    boolean isStartedProducing();
}
