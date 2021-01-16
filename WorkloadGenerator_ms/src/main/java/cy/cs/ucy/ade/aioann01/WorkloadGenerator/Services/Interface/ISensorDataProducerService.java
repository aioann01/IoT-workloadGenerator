package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;

public interface ISensorDataProducerService {


    void initiate(Exchange exchange, ISensorMessageSendService sensorMessageSendService) throws Exception;

    void pause(Exchange exchange)throws Exception;

    void resume(Exchange exchange)throws Exception;

    void terminate(Exchange exchange)throws Exception;

    boolean isStartedProducing();
}
