package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.MockSensorJob;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensorData.MockSensorPrototypeJob;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.DatasetSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.WriterThread;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Component
public class WorkloadGenerator {

    protected int threadPoolSize;

    protected boolean writeToOutputFile;

    protected List<MockSensorPrototype> mockSensorPrototypes;

    protected List<DatasetSensorPrototype> datasetSensorPrototypes;

    protected List<MockSensorPrototypeJob> mockSensorPrototypeJobs;

    protected List<MockSensorJob> mockSensorJobs;

    protected JSONObject configs;

    protected boolean started;

    protected boolean initialized;

    protected WriterThread writerThread;

    public List<MockSensorPrototype> getMockSensorPrototypes() {
        return mockSensorPrototypes;
    }

    public void setMockSensorPrototypes(List<MockSensorPrototype> mockSensorPrototypes) {
        this.mockSensorPrototypes = mockSensorPrototypes;
    }

    public List<DatasetSensorPrototype> getDatasetSensorPrototypes() {
        return datasetSensorPrototypes;
    }

    public void setDatasetSensorPrototypes(List<DatasetSensorPrototype> datasetSensorPrototypes) {
        this.datasetSensorPrototypes = datasetSensorPrototypes;
    }


    public WriterThread getWriterThread() {
        return writerThread;
    }

    public void setWriterThread(WriterThread writerThread) {
        this.writerThread = writerThread;
    }



    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public List<MockSensorPrototypeJob> getMockSensorPrototypeJobs() {
        return mockSensorPrototypeJobs;
    }

    public void setMockSensorPrototypeJobs(List<MockSensorPrototypeJob> mockSensorPrototypeJobs) {
        this.mockSensorPrototypeJobs = mockSensorPrototypeJobs;
    }

    public JSONObject getConfigs() {
        return configs;
    }

    public void setConfigs(JSONObject configs) {
        this.configs = configs;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public List<MockSensorJob> getMockSensorJobs() {
        return mockSensorJobs;
    }

    public void setMockSensorJobs(List<MockSensorJob> mockSensorJobs) {
        this.mockSensorJobs = mockSensorJobs;
    }

    public boolean isWriteToOutputFile() {
        return writeToOutputFile;
    }

    public void setWriteToOutputFile(boolean writeToOutputFile) {
        this.writeToOutputFile = writeToOutputFile;
    }
}
