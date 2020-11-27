package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Controller;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.management.OperatingSystemMXBean;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ResponseMessage;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.SuccessResponseMessage;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData.AddMockSensorsRequest;
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

    private final AtomicLong counter = new AtomicLong();
    public static boolean started = false;
    private boolean started2 = false;

    @Autowired
    public MockSensorPrototypeService mockSensorPrototypeService;

    @Autowired
    public MockSensorService mockSensorService;

    @Autowired
    public WorkloadGeneratorService workloadGeneratorService;


    @PostMapping("/workloadGenerator/start")
    public Exchange startWorkloadGenerator(Exchange exchange, @RequestParam(value = "delay", required = false) Integer delay) {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
// What % CPU load this current JVM is taking, from 0.0-1.0
        System.out.println("*******CPUload: "+osBean.getProcessCpuLoad()+"******");

// What % load the overall system is at, from 0.0-1.0
        System.out.println("*******CPUload: "+osBean.getSystemCpuLoad()+"******");
        System.out.println("*******Threads: "+Thread.getAllStackTraces().keySet().size()+"******");
        int nbRunning = 0;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getState()==Thread.State.RUNNABLE) nbRunning++;
        }
        System.out.println("*******NBUThreads: "+nbRunning);


        String errorMessage = null;
        if (!workloadGeneratorService.isStarted()) {
            if (!workloadGeneratorService.isInitialized()) {
                try {
                    workloadGeneratorService.readConfigs(exchange);
                    workloadGeneratorService.initializeReceiverServiceConfiguration(exchange);
                    workloadGeneratorService.readSensorDataConfigs(exchange);
                } catch (Exception exception) {
                    errorMessage = exception.getMessage();
                    log.error(errorMessage);
                    Utils.setInternalServerErrorOnExchange(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
                    return exchange;
                }
            }
        } else {
            errorMessage = "The workload generator is already running!";
            log.error(errorMessage);
            Utils.setValidationExceptionOnExchange(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        exchange.setProperty(DELAY, delay);
        try {
            workloadGeneratorService.start(exchange);
        } catch (Exception exception) {
            return exchange;
        }
        if(workloadGeneratorService.isStarted()){
            SuccessResponseMessage successResponseMessage = new SuccessResponseMessage("Workload Generator has been successfully started!");
            exchange.setBody(successResponseMessage);
            log.info("Generator Started");}
        else{
            errorMessage = "WorkloadGenerator could not succesfully start.Please check logs for errors of sensorPrototypes";
            log.error(errorMessage);
            Utils.setInternalServerErrorOnExchange(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
        }
        return exchange;
    }


    @PostMapping("/workloadGenerator/stop")
    public Exchange stopWorkloadGenerator(Exchange exchange, @RequestParam(value = "delay", required = false) Integer delay) {
        String errorMessage = null;
        try {
            if (!workloadGeneratorService.isStarted()) {
                errorMessage = "The workload generator is not running!";
                log.error(errorMessage);
                Utils.setValidationExceptionOnExchange(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
                return exchange;
            }
            if (delay != null)
                TimeUnit.SECONDS.sleep(delay);
            workloadGeneratorService.stop(exchange);
            return exchange;
        } catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            errorMessage = "Workload Generator could not be stopped due to :" + exception.getMessage();
            Utils.setInternalServerErrorOnExchange(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
    }


    @PostMapping("/workloadGenerator/restart")
    public Exchange restartWorkloadGenerator(Exchange exchange, @RequestParam(value = "delay", required = false) Integer delay) {
        String errorMessage = null;
        try {
            workloadGeneratorService.restart(exchange);
            SuccessResponseMessage successResponseMessage = new SuccessResponseMessage("Workload Generator has been successfully restarted!");
            exchange.setBody(successResponseMessage);
        } catch (Exception exception) {
            log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            errorMessage = "Workload Generator could not be restarted due to :" + exception.getMessage();
            Utils.setInternalServerErrorOnExchange(exchange,errorMessage,UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        return exchange;
    }



    @GetMapping("mockSensors")
    public Exchange retrieveMockSensors(Exchange exchange, @RequestParam(required = false) String sensorId){
        log.info("Request to {GET MockSensors} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationExceptionOnExchange(exchange,"Workload Generator it isn't working", UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        for(ISensorDataProducerService sensorDataProducerService : workloadGeneratorService.getSensorDataProducerServices())
            if(sensorDataProducerService instanceof ProduceMockSensorDataService) {
                if (sensorId != null) {
                    log.debug("id query param is present:" + sensorId);
                    mockSensorService.findMockSensorById(exchange, sensorId);
                } else {
                    mockSensorService.retrieveAllMockSensors(exchange);
                }
            }else {
                Utils.setValidationExceptionOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
            }
        return exchange;
    }


    @PostMapping("mockSensors")
    public Exchange addMockSensor(Exchange exchange, @RequestBody(required = true) AddMockSensorsRequest addMockSensorsRequest) {
        log.info("Request to {POST mockSensors} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationExceptionOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        for(ISensorDataProducerService sensorDataProducerService:workloadGeneratorService.getSensorDataProducerServices())
            if(sensorDataProducerService instanceof ProduceMockSensorDataService) {
                if (addMockSensorsRequest != null && addMockSensorsRequest.getMockSensorPrototypeName() != null) {
                    ((ProduceMockSensorDataService) sensorDataProducerService).createMockSensorJobsForMockSensorPrototype(exchange, addMockSensorsRequest.getMockSensorPrototypeName(), addMockSensorsRequest.getQuantity());
                }else {
                    exchange.setProperty(ERROR_MESSAGE,"MockSensorPrototype name for the mockSensors has not been provided in the payload");
                    exchange.setProperty(ERROR_MESSAGE_TYPE,VALIDATION_ERROR);
                    exchange.setHttpStatus(HTTP_BAD_REQUEST);}
            }else {
                Utils.setValidationExceptionOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
            }
        return exchange;
    }


    @DeleteMapping("mockSensors/{mockSensorId}")
    public Exchange deleteMockSensor(Exchange exchange,@PathVariable(name = "mockSensorId", required = true) String mockSensorId) {
        log.info("Request to {DELETE mockSensors} received");
        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationExceptionOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        for(ISensorDataProducerService sensorDataProducerService:workloadGeneratorService.getSensorDataProducerServices())
            if(sensorDataProducerService instanceof ProduceMockSensorDataService) {
                ((ProduceMockSensorDataService) sensorDataProducerService).terminateMockSensorJob(exchange, mockSensorId);
            }else {
                Utils.setValidationExceptionOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
            }
        return  exchange;
    }


    @GetMapping("mockSensorPrototypes")
    public Exchange retrieveMockSensorPrototypes(Exchange exchange,@RequestParam(required = false)  String name){
        log.info("Request to {GET mockSensorPrototypes} received");
        if(!workloadGeneratorService.isStarted()){
            Utils.setValidationExceptionOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;}
        for(ISensorDataProducerService sensorDataProducerService:workloadGeneratorService.getSensorDataProducerServices())
            if(sensorDataProducerService instanceof ProduceMockSensorDataService) {

                if(name!=null){
                    log.debug("name query param is present:" +name);
                    mockSensorPrototypeService.findMockSensorPrototypeByName(exchange,name);
                }
                else {
                    mockSensorPrototypeService.retrieveAllMockSensorPrototypes(exchange);
                }
            }else {
                Utils.setValidationExceptionOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
            }
        return exchange;
    }



    @PostMapping("mockSensorPrototypes")
    public Exchange addSensorPrototype(Exchange exchange,@RequestBody(required = true) MockSensorPrototype mockSensorPrototype) {
        log.info("Request to {POST sensorPrototypes} received");

        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationExceptionOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        for(ISensorDataProducerService sensorDataProducerService:workloadGeneratorService.getSensorDataProducerServices())
            if(sensorDataProducerService instanceof ProduceMockSensorDataService) {
                if (mockSensorPrototype != null && mockSensorPrototype.getSensorPrototypeName() != null) {
                    ((ProduceMockSensorDataService) sensorDataProducerService).createMockSensorJobsForNewSensorPrototype(exchange,mockSensorPrototype);
                } else {
                    Utils.setValidationExceptionOnExchange(exchange,"MockSensorPrototype name for the mockSensors has not been provided in the payload",VALIDATION_ERROR);
                }
            }else {
                Utils.setValidationExceptionOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
            }
        return exchange;

    }



    @DeleteMapping("mockSensorPrototypes/{mockSensorPrototypeName}")
    public Exchange deleteMockSensorPrototype(Exchange exchange,@PathVariable(name = "mockSensorPrototypeName", required = true) String mockSensorPrototypeName) {
        log.info("Request to {DELETE mockSensorPrototypes} received");

        if (!workloadGeneratorService.isStarted()) {
            Utils.setValidationExceptionOnExchange(exchange,"Workload Generator it isn't working",UNEXPECTED_ERROR_OCCURRED);
            return exchange;
        }
        for(ISensorDataProducerService sensorDataProducerService:workloadGeneratorService.getSensorDataProducerServices())
            if(sensorDataProducerService instanceof ProduceMockSensorDataService) {

                ((ProduceMockSensorDataService) sensorDataProducerService).terminateMockSensorJobsForMockSensorPrototype(exchange, mockSensorPrototypeName);
            }else {
                Utils.setValidationExceptionOnExchange(exchange,"MockSensor Operations are not supported for the provided configs_file.",UNEXPECTED_ERROR_OCCURRED);
            }
        return  exchange;
    }


}
