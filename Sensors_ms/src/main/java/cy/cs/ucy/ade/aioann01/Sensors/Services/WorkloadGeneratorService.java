package cy.cs.ucy.ade.aioann01.Sensors.Services;

import cy.cs.ucy.ade.aioann01.Sensors.Model.GetSensorPrototypesResponse;
import cy.cs.ucy.ade.aioann01.Sensors.Model.GetSensorsResponse;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.Sensors.Model.MockSensor;
import cy.cs.ucy.ade.aioann01.Sensors.Model.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.Sensors.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.EXCEPTION_CAUGHT;
import static cy.cs.ucy.ade.aioann01.Sensors.Utils.SensorConstants.*;

@Service
public class WorkloadGeneratorService {

    @Autowired
    RestTemplate restTemplate = new RestTemplate();


    private static final Logger log = LoggerFactory.getLogger(WorkloadGeneratorService.class);

    private static String workloadGeneratorServiceHost;

    private static String workloadGeneratorServicePort;


    public void prepareRequest(Exchange exchange,String endpoint,String operationName)throws ValidationException,Exception{

        if(workloadGeneratorServicePort==null)
            workloadGeneratorServicePort= Utils.readPropertyFromConfigs(WORKLOAD_GENERATOR_SERVICE_PORT_PROPERTY_NAME);
        if(workloadGeneratorServiceHost==null)
            workloadGeneratorServiceHost = Utils.readPropertyFromConfigs(WORKLOAD_GENERATOR_SERVICE_HOST_PROPERTY_NAME);

        StringBuilder url = new StringBuilder();
        url.append("http://")
                .append(workloadGeneratorServiceHost)
                .append(":")
                .append(workloadGeneratorServicePort)
                .append(WORKLOAD_GENERATOR_HELPER_URI)
                .append(endpoint);

        switch (operationName){
            case GET_OPERATION:
                exchange.setProperty(REQUEST_HTTP_METHOD,REQUEST_GET_HTTP_METHOD);
                if(endpoint.equals(MOCK_SENSORS_ENDPOINT)) {
                    if(exchange.getProperty(MOCK_SENSOR_ID)!=null) {
                        url.append(HTTP_QUERY_PARAMETER+ID_QUERY_PARAMETER + (String) exchange.getProperty(MOCK_SENSOR_ID));
                        exchange.setProperty(REQUEST_RETURN_TYPE, new ParameterizedTypeReference<MockSensor>(){
                        });
                    }
                    else
                        exchange.setProperty(REQUEST_RETURN_TYPE, new ParameterizedTypeReference<GetSensorsResponse>(){
                        });
                }
                else {
                    if(exchange.getProperty(MOCK_SENSOR_PROTOTYPE_NAME)!=null) {
                        url.append(HTTP_QUERY_PARAMETER+NAME_QUERY_PARAMETER + (String) exchange.getProperty(MOCK_SENSOR_PROTOTYPE_NAME));
                        exchange.setProperty(REQUEST_RETURN_TYPE, new ParameterizedTypeReference<MockSensorPrototype>(){
                        });
                    }
                    else
                        exchange.setProperty(REQUEST_RETURN_TYPE, new ParameterizedTypeReference<GetSensorPrototypesResponse>(){
                        });
                }
                break;
            case ADD_OPERATION:
                exchange.setProperty(REQUEST_HTTP_METHOD,REQUEST_POST_HTTP_METHOD);
                break;
            case DELETE_OPERATION:
                exchange.setProperty(REQUEST_HTTP_METHOD,REQUEST_DELETE_HTTP_METHOD);
                if(endpoint.equals(MOCK_SENSORS_ENDPOINT))
                    url.append("/"+exchange.getProperty(MOCK_SENSOR_ID));
                else
                    url.append("/"+exchange.getProperty(MOCK_SENSOR_PROTOTYPE_NAME));
                break;

            default:Utils.buildValidationException(exchange,"Invalid operation Name to prepare request for WorkloadGeneratorHelper",VALIDATION_ERROR);
        }
        exchange.setProperty(REQUEST_URI,url.toString());
        log.debug("Url created:"  + url.toString());
    }


    public void sendRequest(Exchange exchange,String toUrl)throws ValidationException,Exception{
        log.debug("Sending request to :" + toUrl);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.add(INTER_MS_CALL_HEADER, "true");
        try{
            if(exchange.getProperty(REQUEST_HTTP_METHOD).equals(REQUEST_GET_HTTP_METHOD)){
                HttpEntity requestEntity = new HttpEntity<>(requestHeaders);
                ResponseEntity workloadGeneratorResponseEntity = restTemplate.exchange(toUrl,
                        (HttpMethod) exchange.getProperty(REQUEST_HTTP_METHOD),
                        requestEntity,
                        exchange.getProperty(REQUEST_RETURN_TYPE)==null?null:(ParameterizedTypeReference)exchange.getProperty(REQUEST_RETURN_TYPE));
                exchange.setBody(workloadGeneratorResponseEntity.getBody());}

            else if(exchange.getProperty(REQUEST_HTTP_METHOD).equals(REQUEST_POST_HTTP_METHOD)){

                ResponseEntity  workloadGeneratorResponseEntity= restTemplate.postForEntity(toUrl,exchange.getProperty(REQUEST_PAYLOAD),Object.class);
                // HttpEntity<AddSensorsRequest> requestEntity = new HttpEntity<AddSensorsRequest>((AddSensorsRequest)exchange.getProperty(REQUEST_PAYLOAD,AddSensorsRequest.class));
//                ResponseEntity workloadGeneratorResponseEntity = restTemplate.exchange(toUrl,
//                        (HttpMethod) exchange.getProperty(REQUEST_HTTP_METHOD),
//                        requestEntity,
//                        Object.class);
                if(workloadGeneratorResponseEntity.getStatusCode().equals(HTTP_CREATED)){
                    exchange.setHttpStatus(workloadGeneratorResponseEntity.getStatusCode());}
            }
            else{
                HttpEntity requestEntity = new HttpEntity<>(requestHeaders);
                ResponseEntity workloadGeneratorResponseEntity = restTemplate.exchange(toUrl,
                        (HttpMethod) exchange.getProperty(REQUEST_HTTP_METHOD),
                        requestEntity,
                        Object.class);
                if(workloadGeneratorResponseEntity.getStatusCode().equals(HTTP_NO_CONTENT)){
                    exchange.setHttpStatus(workloadGeneratorResponseEntity.getStatusCode());}}
        }
        catch (Exception exception){
            log.error(EXCEPTION_CAUGHT+exception.getMessage(),exception);
            Utils.setExchangeFromHttpClientErrorException(exchange,exception);
            throw exception;
        }
    }


    @Bean(name = "WorkloadGeneratorServiceBean")
    @Scope("prototype")
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }

}
