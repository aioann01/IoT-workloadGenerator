package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SensorProtototypeServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.DatasetSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.ReplayDatasetSensorPrototypeThread;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.WorkloadGenerator;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.DatasetSensorPrototypeService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorDataProducerService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorMessageSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReplaySensorCSVDataSetService implements ISensorDataProducerService {
    private WorkloadGenerator workloadGenerator;
    private DatasetSensorPrototypeService datasetSensorPrototypeService;
    private List<ReplayDatasetSensorPrototypeThread> replayDatasetSensorPrototypeThreads;
    private List<DatasetSensorPrototype> datasetSensorPrototypes;
    private static final Logger log= LoggerFactory.getLogger(ReplaySensorCSVDataSetService.class);
    private boolean startedProducing = false;

    public ReplaySensorCSVDataSetService(WorkloadGenerator workloadGenerator,DatasetSensorPrototypeService datasetSensorPrototypeService, List<DatasetSensorPrototype> datasetSensorPrototypes){
        this.datasetSensorPrototypeService = datasetSensorPrototypeService;
        this.datasetSensorPrototypes = datasetSensorPrototypes;
        this.workloadGenerator = workloadGenerator;
        replayDatasetSensorPrototypeThreads = new ArrayList<>();

    }


    @Override
    public void initiate(Exchange exchange, ISensorMessageSendService sensorMessageSendService) throws Exception {
        log.debug("Entered initiate()");
        this.datasetSensorPrototypeService.setSensorMessageSendService(sensorMessageSendService);
        for(DatasetSensorPrototype datasetSensorPrototype:datasetSensorPrototypes){
            ReplayDatasetSensorPrototypeThread replayDatasetSensorPrototypeThread = new ReplayDatasetSensorPrototypeThread(datasetSensorPrototypeService,datasetSensorPrototype);
            if(replayDatasetSensorPrototypeThread.getDatasetSensorPrototypeIsCorrectlySet()){
                replayDatasetSensorPrototypeThreads.add(replayDatasetSensorPrototypeThread);
                replayDatasetSensorPrototypeThread.start();
                startedProducing = true;
                if(!workloadGenerator.isInitialized())
                    workloadGenerator.setInitialized(true);}
        }
    }

    @Override
    public void pause(Exchange exchange) throws Exception {
        for(ReplayDatasetSensorPrototypeThread replayDatasetSensorPrototypeThread:replayDatasetSensorPrototypeThreads) {
            if(!replayDatasetSensorPrototypeThread.isFinished())
                replayDatasetSensorPrototypeThread.pause();
        }
        startedProducing = false;
    }

    @Override
    public void resume(Exchange exchange) throws Exception {
        for(ReplayDatasetSensorPrototypeThread replayDatasetSensorPrototypeThread:replayDatasetSensorPrototypeThreads) {
            if(!replayDatasetSensorPrototypeThread.isFinished())
                replayDatasetSensorPrototypeThread.resume2();
        }
        startedProducing = true;
    }

    @Override
    public void terminate(Exchange exchange) throws Exception {
        for(ReplayDatasetSensorPrototypeThread replayDatasetSensorPrototypeThread:replayDatasetSensorPrototypeThreads) {
            if(!replayDatasetSensorPrototypeThread.isFinished())
                replayDatasetSensorPrototypeThread.terminate();
        }
    }

    @Override
    public boolean isStartedProducing() {
        return startedProducing;
    }
}
