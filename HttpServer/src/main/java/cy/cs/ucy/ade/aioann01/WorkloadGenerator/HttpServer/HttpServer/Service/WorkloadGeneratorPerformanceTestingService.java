package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.Service;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.SensorMessageDetails;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.SensorPrototypeAccuracyEvaluationRequest;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel.SensorMetrics;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.Thread.WorkloadGeneratorAccuracyPerformanceThread;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Service
public class WorkloadGeneratorPerformanceTestingService {

    @Autowired
    private WorkloadGeneratorAccuracyPerformanceThread workloadGeneratorAccuracyPerformanceThread;

    public WorkloadGeneratorAccuracyPerformanceThread getWorkloadGeneratorAccuracyPerformanceThread() {
        return workloadGeneratorAccuracyPerformanceThread;
    }

    public void setWorkloadGeneratorAccuracyPerformanceThread(WorkloadGeneratorAccuracyPerformanceThread workloadGeneratorAccuracyPerformanceThread) {
        this.workloadGeneratorAccuracyPerformanceThread = workloadGeneratorAccuracyPerformanceThread;
    }

    public void prepareEvaluation(SensorPrototypeAccuracyEvaluationRequest request){
        this.workloadGeneratorAccuracyPerformanceThread.setSensorMessageIntervalTime(request.getMessageIntervalTime());
        this.workloadGeneratorAccuracyPerformanceThread.setSensorPrototypeNameForEvaluation(request.getSensorPrototypeName());
    }

    public void resetEvaluation(){
        workloadGeneratorAccuracyPerformanceThread.reset();
    }

    public void evaluateAccuracy(String message, String date, String contentType){
        SensorMessageDetails sensorMessageDetails = new SensorMessageDetails();
        sensorMessageDetails.setMessageDate(date);
        sensorMessageDetails.setMessage(message);
        sensorMessageDetails.setContentType(contentType);
        workloadGeneratorAccuracyPerformanceThread.getBlockingDeque().add(sensorMessageDetails);
    }


    public void printStatistics(){
        try {
            FileWriter jsonWriter = new FileWriter(workloadGeneratorAccuracyPerformanceThread.getSensorPrototypeNameForEvaluation()+"_AccuracyEvaluation.json");
            JSONObject statisticsJSONObject = new JSONObject();
            Double totalSensorsAvgDelay = 0.0;
            int totalSensorsCount =  0;
            while(workloadGeneratorAccuracyPerformanceThread.getBlockingDeque().size() !=0 );
            for(Map.Entry<String, SensorMetrics> sensorMetrics : workloadGeneratorAccuracyPerformanceThread.getSensorsMetricsMap().entrySet()){
                Double sensorAvgDelay = sensorMetrics.getValue().getSensorsMessagesDelaySum()/sensorMetrics.getValue().getSensorsMessagesCount();
                totalSensorsAvgDelay += sensorAvgDelay;
                totalSensorsCount += 1;
            }
            statisticsJSONObject.put("avgSensorMessageDelay", totalSensorsAvgDelay/totalSensorsCount);
            jsonWriter.append(statisticsJSONObject.toString());
            jsonWriter.flush();
            jsonWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
