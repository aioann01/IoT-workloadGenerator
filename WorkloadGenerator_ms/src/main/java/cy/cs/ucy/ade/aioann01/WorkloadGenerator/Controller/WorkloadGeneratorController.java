package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Controller;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.SuccessResponseMessage;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.AddMockSensorsRequest;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices.MockSensorPrototypeService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices.MockSensorService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SensorProtototypeServices.ProduceMockSensorDataService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static  cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;


@RestController
public class WorkloadGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(WorkloadGeneratorController.class);

    @Autowired
    public MockSensorPrototypeService mockSensorPrototypeService;

    @Autowired
    public MockSensorService mockSensorService;

    @Autowired
    public WorkloadGeneratorService workloadGeneratorService;



    /**
     * @Author:Andreas Ioannou
     * @param delay
     * The purpose of this API is to start the workload generator with the provided properties inside configs_file.json
     ***/
    @PostMapping("/workloadGenerator/start")
    public Exchange startWorkloadGenerator(Exchange exchange, @RequestParam(value = "delay", required = false) Integer delay) {
        Utils.printHostedMachineStats();
        String errorMessage = null;
        if (!workloadGeneratorService.isStarted()) {
            if (!workloadGeneratorService.isInitialized()) {
                try {
                    workloadGeneratorService.readConfigs(exchange);
                    workloadGeneratorService.processOutputProtocolConfigurationsAndEstablishConnections(exchange);
                    workloadGeneratorService.readSensorDataConfigs(exchange);
                } catch (Exception exception) {
                    errorMessage = exception.getMessage();
                    log.error(errorMessage);
                    Utils.setInternalServerErrorOnExchange(exchange, errorMessage, UNEXPECTED_ERROR_OCCURRED);
                    return exchange;
                }
            }
        }else {
            errorMessage = "The workload generator is already running!";
            log.error(errorMessage);
            Utils.setValidationErrorOnExchange(exchange, errorMessage, UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        try {
            if (delay != null)
                TimeUnit.SECONDS.sleep(delay);
            workloadGeneratorService.start(exchange);
        } catch (Exception exception) {
            return exchange;
        }
        if(workloadGeneratorService.isStarted()){
            SuccessResponseMessage successResponseMessage = new SuccessResponseMessage("Workload Generator has been successfully started!");
            exchange.setBody(successResponseMessage);
            log.info("Generator Started");}
        else{
            errorMessage = "WorkloadGenerator could not start successfully. Please check logs for errors of sensorPrototypes";
            log.error(errorMessage);
            Utils.setInternalServerErrorOnExchange(exchange, errorMessage, UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;
    }


    /**
     * @Author:Andreas Ioannou
     * @param delay
     * The purpose of this API is to stop the execution of the workload generator temporarily
     ***/
    @PostMapping("/workloadGenerator/stop")
    public Exchange stopWorkloadGenerator(Exchange exchange, @RequestParam(value = "delay", required = false) Integer delay) {
        String errorMessage = null;
        try {
            if (!workloadGeneratorService.isStarted()) {
                errorMessage = "The workload generator is not running!";
                log.error(errorMessage);
                Utils.setValidationErrorOnExchange(exchange, errorMessage, UNEXPECTED_ERROR_OCCURRED);
                return exchange;
            }
            if (delay != null)
                TimeUnit.SECONDS.sleep(delay);
            workloadGeneratorService.stop(exchange);
            return exchange;
        }catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            errorMessage = "Workload Generator could not be stopped due to :" + exception.getMessage();
            Utils.setInternalServerErrorOnExchange(exchange, errorMessage, UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
    }


    /**
     * @Author:Andreas Ioannou
     * @param delay
     * @return
     * The purpose of this API is to restart the workload generator by using the possibly changed configs_file.json
     ***/
    @PostMapping("/workloadGenerator/restart")
    public Exchange restartWorkloadGenerator(Exchange exchange, @RequestParam(value = "delay", required = false) Integer delay) {
        String errorMessage = null;
        try {
            exchange.setProperty(DELAY, delay);
            workloadGeneratorService.restart(exchange);
        } catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            errorMessage = "Workload Generator could not be restarted due to :" + exception.getMessage();
            Utils.setInternalServerErrorOnExchange(exchange, errorMessage, UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        SuccessResponseMessage successResponseMessage = new SuccessResponseMessage("Workload Generator has been successfully restarted!");
        exchange.setBody(successResponseMessage);
        return exchange;
    }


    /**
     * @Author:Andreas Ioannou
     * @param sensorId
     * The purpose of this API is to retrieve sensors information
     ***/
    @GetMapping("mockSensors")
    public Exchange retrieveMockSensors(Exchange exchange, @RequestParam(required = false) String sensorId){
        log.info("Request to {GET MockSensors} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationErrorOnExchange(exchange,"Workload Generator it isn't working", UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }

        Optional<ProduceMockSensorDataService> produceMockSensorDataServiceOptional = Utils.findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(workloadGeneratorService.getSensorDataProducerServices());
        if(produceMockSensorDataServiceOptional.isPresent()){
            if (sensorId != null) {
                log.debug("id query param is present:" + sensorId);
                mockSensorService.findMockSensorById(exchange, sensorId);
            } else {
                mockSensorService.retrieveAllMockSensors(exchange);
            }
        }else {
            Utils.setValidationErrorOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.", UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;
    }


    /**
     * @Author:Andreas Ioannou
     * @param addMockSensorsRequest
     * The purpose of this API is create new sensors for a mockSensorPrototype
     ***/
    @PostMapping("mockSensors")
    public Exchange addMockSensor(Exchange exchange, @RequestBody(required = true) AddMockSensorsRequest addMockSensorsRequest) {
        log.info("Request to {POST mockSensors} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationErrorOnExchange(exchange,"Workload Generator it isn't working", UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }

        Optional<ProduceMockSensorDataService> produceMockSensorDataServiceOptional = Utils.findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(workloadGeneratorService.getSensorDataProducerServices());
        if(produceMockSensorDataServiceOptional.isPresent()){
            ProduceMockSensorDataService produceMockSensorDataService = produceMockSensorDataServiceOptional.get();
            if (addMockSensorsRequest != null && addMockSensorsRequest.getMockSensorPrototypeName() == null) {
                Utils.setValidationErrorOnExchange(exchange, "MockSensorPrototype name for the mockSensors has not been provided in the payload", VALIDATION_ERROR);
            }else if (addMockSensorsRequest != null && addMockSensorsRequest.getQuantity() == null){
                Utils.setValidationErrorOnExchange(exchange, "Quantity for the mockSensors has not been provided in the payload", VALIDATION_ERROR);
            }else{
                produceMockSensorDataService.createMockSensorJobsForMockSensorPrototype(exchange, addMockSensorsRequest.getMockSensorPrototypeName(), addMockSensorsRequest.getQuantity());
            }
        }
        else{
            Utils.setValidationErrorOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.", UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;
    }


    /**
     * @Author:Andreas Ioannou
     * @param mockSensorId
     * The purpose of this API is to delete the specified mockSensor
     ***/
    @DeleteMapping("mockSensors/{mockSensorId}")
    public Exchange deleteMockSensor(Exchange exchange, @PathVariable(name = "mockSensorId", required = true) String mockSensorId) {
        log.info("Request to {DELETE mockSensors} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationErrorOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }

        Optional<ProduceMockSensorDataService> produceMockSensorDataServiceOptional = Utils.findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(workloadGeneratorService.getSensorDataProducerServices());
        if(produceMockSensorDataServiceOptional.isPresent()){
            ProduceMockSensorDataService produceMockSensorDataService = produceMockSensorDataServiceOptional.get();
            produceMockSensorDataService.terminateMockSensorJob(exchange, mockSensorId);
        }else {
            Utils.setValidationErrorOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;
    }


    /**
     * @Author:Andreas Ioannou
     * @param mockSensorPrototypeName
     * The purpose of this API is retrieve the specified or all the mockSensorPrototypes info
     ***/
    @GetMapping("mockSensorPrototypes")
    public Exchange retrieveMockSensorPrototypes(Exchange exchange, @RequestParam(required = false) String mockSensorPrototypeName){
        log.info("Request to {GET mockSensorPrototypes} received");
        if(!workloadGeneratorService.isStarted()){
            Utils.setValidationErrorOnExchange(exchange,"Workload Generator it isn't working", UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        Optional<ProduceMockSensorDataService> produceMockSensorDataServiceOptional = Utils.findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(workloadGeneratorService.getSensorDataProducerServices());
        if(produceMockSensorDataServiceOptional.isPresent()){
            if(mockSensorPrototypeName != null){
                log.debug("mockSensorPrototypeName query param is present:" +mockSensorPrototypeName);
                mockSensorPrototypeService.findMockSensorPrototypeByName(exchange, mockSensorPrototypeName);
            }
            else {
                mockSensorPrototypeService.retrieveAllMockSensorPrototypes(exchange);
            }
        }else {
            Utils.setValidationErrorOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;
    }


    /**
     * @Author Andreas Ioannou
     * @param mockSensorPrototype
     * The purpose of this API isto  create a new mockSensorPrototype and its sensors
     ***/
    @PostMapping("mockSensorPrototypes")
    public Exchange addSensorPrototype(Exchange exchange,@RequestBody(required = true) MockSensorPrototype mockSensorPrototype) {
        log.info("Request to {POST sensorPrototypes} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationErrorOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        Optional<ProduceMockSensorDataService> produceMockSensorDataServiceOptional = Utils.findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(workloadGeneratorService.getSensorDataProducerServices());
        if(produceMockSensorDataServiceOptional.isPresent()){
            if (mockSensorPrototype != null && mockSensorPrototype.getSensorPrototypeName() != null) {
                ProduceMockSensorDataService produceMockSensorDataService = produceMockSensorDataServiceOptional.get();
                produceMockSensorDataService.createMockSensorJobsForNewSensorPrototype(exchange,mockSensorPrototype);
            } else {
                Utils.setValidationErrorOnExchange(exchange,"MockSensorPrototype name for the mockSensors has not been provided in the payload",VALIDATION_ERROR);
            }
        }else {
            Utils.setValidationErrorOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;

    }


    /**
     * @Author:Andreas Ioannou
     * @param mockSensorPrototypeName
     * The purpose of this API is delete a mockSensorPrototype and its sensors
     ***/
    @DeleteMapping("mockSensorPrototypes/{mockSensorPrototypeName}")
    public Exchange deleteMockSensorPrototype(Exchange exchange, @PathVariable(name = "mockSensorPrototypeName", required = true) String mockSensorPrototypeName) {
        log.info("Request to {DELETE mockSensorPrototypes} received");

        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationErrorOnExchange(exchange,"Workload Generator it isn't working", UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        Optional<ProduceMockSensorDataService> produceMockSensorDataServiceOptional = Utils.findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(workloadGeneratorService.getSensorDataProducerServices());
        if(produceMockSensorDataServiceOptional.isPresent()){
            ProduceMockSensorDataService produceMockSensorDataService = produceMockSensorDataServiceOptional.get();
            produceMockSensorDataService.terminateMockSensorJobsForMockSensorPrototype(exchange, mockSensorPrototypeName);
        }else {
            Utils.setValidationErrorOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
        }
        return  exchange;
    }


}
