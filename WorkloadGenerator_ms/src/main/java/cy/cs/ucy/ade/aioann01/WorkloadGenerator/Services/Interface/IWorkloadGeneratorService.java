package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;

public interface IWorkloadGeneratorService {

     void readConfigs(Exchange exchange) throws Exception;

     void processOutputProtocolConfigurationsAndEstablishConnections(Exchange exchange) throws Exception;

     void readSensorDataConfigs(Exchange exchange) throws Exception;

     void start(Exchange exchange)throws Exception;

     void stop(Exchange exchange)throws Exception;

     void restart(Exchange exchange)throws Exception;

}
