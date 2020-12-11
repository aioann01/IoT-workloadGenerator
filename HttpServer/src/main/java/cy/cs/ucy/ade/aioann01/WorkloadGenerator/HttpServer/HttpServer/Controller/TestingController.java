package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.Controller;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.ConstantGenerationRate;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.SensorPrototypeAccuracyEvaluationRequest;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.SuccessResponse;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.Service.WorkloadGeneratorPerformanceTestingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestingController {

    private static final Logger log= LoggerFactory.getLogger(TestingController.class);

    @Autowired
    private WorkloadGeneratorPerformanceTestingService workloadGeneratorPerformanceTestingService;
    @ExceptionHandler()
    @PostMapping("/testing")
    public ResponseEntity test(@RequestBody String body, @RequestHeader(HttpHeaders.DATE) String date, @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType){
        log.trace("Received message with body:" +body +" at Date:" +date);
        workloadGeneratorPerformanceTestingService.evaluateAccuracy(body, date, contentType);
        return ResponseEntity.ok(new SuccessResponse("Success"));
    }

    @PostMapping("/testing/prepare")
    public ResponseEntity prepareTesting(@RequestBody SensorPrototypeAccuracyEvaluationRequest request){
        if(!workloadGeneratorPerformanceTestingService.getWorkloadGeneratorAccuracyPerformanceThread().isAlive()){
            workloadGeneratorPerformanceTestingService.getWorkloadGeneratorAccuracyPerformanceThread().start();
        }

        workloadGeneratorPerformanceTestingService.prepareEvaluation(request);
      // return ResponseEntity.ok().build();
        return ResponseEntity.ok(new SuccessResponse("Success"));
    }

    @PostMapping("/testing/reset")
    public ResponseEntity resetTesting(@RequestBody String empty){
        workloadGeneratorPerformanceTestingService.resetEvaluation();
        return ResponseEntity.ok(new SuccessResponse("Success"));
    }

    @PostMapping("/testing/printStatistics")
    public ResponseEntity printStatistics(@RequestBody String empty){
        workloadGeneratorPerformanceTestingService.printStatistics();
        return ResponseEntity.ok(new SuccessResponse("Success"));
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public void onErr400(@RequestHeader(value="ETag", required=false) String ETag,
                         UnsatisfiedServletRequestParameterException ex) {
        if(ETag == null) {
            // Ok the problem was ETag Header : give your informational message
        } else {
            // It is another error 400  : simply say request is incorrect or use ex
        }
    }

}
