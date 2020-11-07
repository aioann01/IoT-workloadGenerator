package cy.cs.ucy.ade.aioann01.Sensors.Controller;


import cy.cs.ucy.ade.aioann01.Sensors.Model.*;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.SuccessResponseMessage;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.Sensors.Services.WorkloadGeneratorService;
import cy.cs.ucy.ade.aioann01.Sensors.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.Sensors.Utils.SensorConstants.*;

@RestController
public class MockSensorController {
    private static final Logger log= LoggerFactory.getLogger(MockSensorController.class);

    @Autowired
    private WorkloadGeneratorService workloadGeneratorService;


    @GetMapping("/mockSensors")
    public  Exchange getMockSensors(Exchange exchange) {

          log.info("Retrieving all mockSensors");
          try {
              workloadGeneratorService.prepareRequest(exchange, MOCK_SENSORS_ENDPOINT, GET_OPERATION);
              String requestUrl = (String) (exchange.getProperty(REQUEST_URI));
              workloadGeneratorService.sendRequest(exchange, requestUrl);
          } catch (ValidationException ve) {
              log.error(VALIDATION_EXCEPTION_CAUGHT + ve.getMessage(), ve);
              return exchange;
          } catch (Exception e) {
              if (exchange.getProperty(EXCEPTION_IS_SET) != null && !(Boolean) exchange.getProperty(EXCEPTION_IS_SET)) {
                  exchange.setProperty(ERROR_MESSAGE, e.getMessage());
                  exchange.setProperty(ERROR_MESSAGE_TYPE, UNEXPECTED_ERROR_OCCURRED);
                  exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                  log.error(UNEXPECTED_EXCEPTION_CAUGHT, e.getMessage(), e);
              } else
                  log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
              return exchange;
          }
          return exchange;
      }


    @GetMapping("/mockSensors/{mockSensorId}")
    public Exchange getMockSensor(Exchange exchange,@PathVariable(value = "mockSensorId" ,required = true) String mockSensorId) {
        log.info("Retrieving mockSensor");
        try {
            if(mockSensorId==null){
                Utils.buildValidationException(exchange,"Mandatory path parameter {mockSensorId} has not been provided",VALIDATION_ERROR);
            }
            exchange.setProperty(MOCK_SENSOR_ID,mockSensorId);
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSORS_ENDPOINT,GET_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
            MockSensor mockSensor = (MockSensor)exchange.getBody();
        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;
    }



    @PostMapping("/mockSensors")
    public Exchange addMockSensor(Exchange exchange,@RequestBody AddMockSensorsRequest addMockSensorsRequest) {
        log.info("Adding MockSensor");
        try {
            if(addMockSensorsRequest==null){
                Utils.buildValidationException(exchange,"Mandatory body payload {mockSensor} has not been provided or is not correct",VALIDATION_ERROR);
            }
            exchange.setProperty(REQUEST_PAYLOAD,addMockSensorsRequest);
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSORS_ENDPOINT,ADD_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
            if(exchange.getHttpStatus().equals(HTTP_CREATED))
                exchange.setBody(new SuccessResponseMessage("MockSensorPrototype was created successfully"));
        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;   }


    @DeleteMapping("/mockSensors/{mockSensorId}")
    public Exchange deleteMockSensor(Exchange exchange,@PathVariable(value = "mockSensorId") String mockSensorId) {
        log.info("Deleting mockSensor with id:"+mockSensorId);
        try {
            if(mockSensorId==null){
                Utils.buildValidationException(exchange,"Mandatory path parameter {mockSensorId} has not been provided",VALIDATION_ERROR);
            }
            exchange.setProperty(MOCK_SENSOR_ID,mockSensorId);
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSORS_ENDPOINT,DELETE_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;    }




    @GetMapping("/mockSensorPrototypes")
    public Exchange getAllMockSensorPrototypes(Exchange exchange) {
        log.info("Retrieving all mockSensorPrototypes");
        try {
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSOR_PROTOTYPES_ENDPOINT,GET_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;
    }



    @GetMapping("/mockSensorPrototypes/{mockSensorPrototypeName}")
    public Exchange getMockSensorPrototype(Exchange exchange,@PathVariable(value = "mockSensorPrototypeName" ,required = true) String mockSensorPrototypeName) {
        log.info("Retrieving mockSensorPrototype");
        try {
            if(mockSensorPrototypeName==null){
                Utils.buildValidationException(exchange,"Mandatory path parameter {mockSensorPrototypeName} has not been provided",VALIDATION_ERROR);
            }
            exchange.setProperty(MOCK_SENSOR_PROTOTYPE_NAME,mockSensorPrototypeName);
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSOR_PROTOTYPES_ENDPOINT,GET_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
            MockSensorPrototype mockSensorPrototype =(MockSensorPrototype)exchange.getBody();
        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;
    }



    @PostMapping("/mockSensorPrototypes")
    public Exchange addMockSensorPrototype(Exchange exchange,@RequestBody MockSensorPrototype mockSensorPrototype){

        log.info("Adding MockSensorPrototype");
        try {
            if(mockSensorPrototype ==null){
                Utils.buildValidationException(exchange,"Mandatory body payload {mockSensorPrototype} has not been provided or is not correct",VALIDATION_ERROR);
            }
            exchange.setProperty(REQUEST_PAYLOAD, mockSensorPrototype);
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSOR_PROTOTYPES_ENDPOINT,ADD_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
            if(exchange.getHttpStatus().equals(HTTP_CREATED))
                exchange.setBody(new SuccessResponseMessage("MockSensorPrototype was created successfully"));

        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;   }


    @DeleteMapping("/mockSensorPrototypes/{mockSensorPrototypeName}")
    public Exchange deleteMockSensorPrototype(Exchange exchange,@PathVariable(value = "mockSensorPrototypeName") String mockSensorPrototypeName) {
        log.info("Deleting mockSensorPrototype with name:"+mockSensorPrototypeName);
        try {
            if(mockSensorPrototypeName==null){
                Utils.buildValidationException(exchange,"Mandatory path parameter {mockSensorPrototypeName} has not been provided",VALIDATION_ERROR);
            }
            exchange.setProperty(MOCK_SENSOR_PROTOTYPE_NAME,mockSensorPrototypeName);
            workloadGeneratorService.prepareRequest(exchange,MOCK_SENSOR_PROTOTYPES_ENDPOINT,DELETE_OPERATION);
            String requestUrl= (String)(exchange.getProperty(REQUEST_URI));
            workloadGeneratorService.sendRequest(exchange,requestUrl);
        }catch ( ValidationException ve){
            log.error(VALIDATION_EXCEPTION_CAUGHT+ve.getMessage(),ve);
            return exchange;
        }
        catch (Exception e){
            if(exchange.getProperty(EXCEPTION_IS_SET)!=null&&!(Boolean)exchange.getProperty(EXCEPTION_IS_SET)){
                exchange.setProperty(ERROR_MESSAGE,e.getMessage());
                exchange.setProperty(ERROR_MESSAGE_TYPE,UNEXPECTED_ERROR_OCCURRED);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                log.error(UNEXPECTED_EXCEPTION_CAUGHT,e.getMessage(),e);
            }
            else
                log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            return exchange;
        }
        return exchange;    }




}




