package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SensorProtototypeServices;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.*;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate.GenerationRateWrapper;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.MockSensor;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.Scenario;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.ScenarioFieldValueInfo;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.ScenarioManager;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.MockSensorJob;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorPrototype.MockSensorPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.MockSensors.MockSensorPrototypeJob;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.WriterThread;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface.ISensorDataProducerService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface.ISensorMessageSendService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices.MockSensorPrototypeService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ProduceMockSensorDataServices.MockSensorService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.GenerationRatesUtils;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.ERROR_MESSAGE;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;

@Service
public class ProduceMockSensorDataService implements ISensorDataProducerService {

    private static final Logger log = LoggerFactory.getLogger(ProduceMockSensorDataService.class);

    private MockSensorPrototypeService mockSensorPrototypeService;

    private MockSensorService mockSensorService;

    private WorkloadGenerator workloadGenerator;

    private ISensorMessageSendService sensorMessageSendService;

    private List<MockSensorPrototype> mockSensorPrototypes;

    private ScenarioManager scenarioManager;

    private Boolean startedProducing = false;

    public ProduceMockSensorDataService(WorkloadGenerator workloadGenerator, MockSensorService mockSensorService, MockSensorPrototypeService mockSensorPrototypeService, List<MockSensorPrototype> mockSensorPrototypes){
        this.mockSensorPrototypeService = mockSensorPrototypeService;
        this.mockSensorService = mockSensorService;
        this.workloadGenerator = workloadGenerator;
        this.mockSensorPrototypes = mockSensorPrototypes;

    }

    @Override
    public void initiate(Exchange exchange,ISensorMessageSendService sensorMessageSendService) throws Exception{
        log.debug("Entered initiate() of ProduceMockSensorDataService");
        this.sensorMessageSendService = sensorMessageSendService;
        String errorMessage;
        List<MockSensorPrototypeJob> mockSensorPrototypeJobs;
        List<MockSensor> mockSensors;
        try {
            mockSensorPrototypeJobs = createAndProcessMockSensorPrototypeJobsFromMockSensorPrototypes();
        }catch (Exception exception){
            errorMessage = EXCEPTION_CAUGHT_WHILE + "while creating mockSensorPrototypeJobs from mockSensorPrototypes: " + exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage, exception);
        }
        try {
            createAndProcessOutputWriterThread(mockSensorPrototypeJobs);
        }catch (Exception exception){
            errorMessage = UNEXPECTED_ERROR_OCCURRED + "while creating output Writer Thread:"+exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage, exception);
        }
        try {
            mockSensors = createMockSensors(mockSensorPrototypeJobs);
        }catch (Exception exception){
            errorMessage = UNEXPECTED_ERROR_OCCURRED + "while creating Mock Sensors: " + exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage, exception);
        }
        mockSensorService.addAllMockSensors(mockSensors);
        mockSensorPrototypes = mockSensorPrototypeJobs.stream()
                .map(mockSensorPrototypeJob -> mockSensorPrototypeJob.getMockSensorPrototype())
                .collect(Collectors.toList());
        mockSensorPrototypeService.addAllMockSensorPrototypes(mockSensorPrototypes);
        workloadGenerator.setMockSensorPrototypeJobs(mockSensorPrototypeJobs);
        Integer delay = (Integer)exchange.getProperty(DELAY);
        if(delay == null)
            delay = 0;
        final int startUpDelay = delay;
        System.out.println(Thread.currentThread().getId()+" "+Thread.currentThread().getName());

//            Integer threadPoolSize = null;
//            try{
//                threadPoolSize =  Integer.parseInt(ApplicationPropertiesUtil.readPropertyFromConfigs(THREAD_POOL_SIZE_PROPERTY));
//            }
//            catch (Exception e){
//                log.info("Thread pool size prioperty not provided. Default thread pool size is the number of sensors.");
//            }
//            if(threadPoolSize == null)
//                threadPoolSize = mockSensorService.getAllMockSensors().size();
        //ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(threadPoolSize);
        //ConcurrentHashMap<String, ScheduledFuture<?>> mockSensorJobFutures = new ConcurrentHashMap<>();

        List<MockSensorJob> mockSensorJobs;
        mockSensorJobs = createAndStartMockSensorJobs();
        try {
            processAndScheduleScenarios(mockSensorJobs);
        }catch (Exception exception){
            errorMessage = EXCEPTION_CAUGHT_WHILE + "while scheduling scenarios "+ exception.getMessage();
            log.error(errorMessage, exception);
        }
        workloadGenerator.setMockSensorJobs(mockSensorJobs);
        startedProducing = true;
    }

    @Override
    public void pause(Exchange exchange) throws Exception {
        log.debug("Entered pause() of ProduceMockSensorDataService");

        if(scenarioManager != null)
            scenarioManager.cancelScenarioJobs();
        cancelAllMockSensorJobs();
        if (workloadGenerator.isWriteToOutputFile()) {
            while (workloadGenerator.getWriterThread().getBlockingDeque().size() != 0) ;
            workloadGenerator.getWriterThread().pause();
        }
        startedProducing = false;

        workloadGenerator.getMockSensorPrototypes().stream()
                .filter(mockSensorPrototype -> mockSensorPrototype.getEvaluateFieldGenerationRate())
                .forEach(mockSensorPrototype -> {
                    List<MockSensorJob> mockSensorJobs = workloadGenerator.getMockSensorJobs().stream()
                            .filter(mockSensorJob -> mockSensorJob.getMockSensor().getMockSensorPrototype().getSensorPrototypeName().equals(mockSensorPrototype.getSensorPrototypeName()))
                            .collect(Collectors.toList());
                    SensorFieldStatistics[] sensorFieldStatistics = mockSensorJobs.get(0).getSensorFieldStatistics();
                    for(int i =1;i<mockSensorJobs.size();++i){
                        for(int j=0;j<mockSensorJobs.get(i).getSensorFieldStatistics().length;++j) {
                            SensorFieldStatistics fieldStatistics = mockSensorJobs.get(i).getSensorFieldStatistics()[j];
                            SensorUtils.mergeSensorFieldStatistics(sensorFieldStatistics[j], fieldStatistics);
                        }
                    }
                    SensorUtils.exportSensorPrototypes(sensorFieldStatistics, 0,mockSensorPrototype.getSensorPrototypeName(), 0);
                });
    }

    @Override
    public void resume(Exchange exchange) throws Exception {
        log.debug("Entered resume() of ProduceMockSensorDataService");

        if(!workloadGenerator.isStarted() && workloadGenerator.isWriteToOutputFile()) {
            workloadGenerator.getWriterThread().resume2();
        }

        workloadGenerator.getMockSensorJobs().parallelStream()
                .forEach(mockSensorJob -> {
                    try {
                        mockSensorJob.resume2();
                    } catch (Exception e) {
                        log.error("SensorJob {"+mockSensorJob.getMockSensor().getId()+ "} could not be resumed due to:"+e.getMessage(), e);
                    }
                });
//        workloadGenerator.getMockSensorJobs().stream()
//                .forEach(mockSensorJob -> {
//                    try{
//                        mockSensorJob.resume2();
//                    }catch (Exception exception){}
//                });
        startedProducing = true;
    }

    @Override
    public void terminate(Exchange exchange) throws Exception {
        log.debug("Entered terminate() of ProduceMockSensorDataService");

        terminateWriterThread();
        clearMockSensorRepository();
        clearMockSensorPrototypeRepository();
        workloadGenerator.getMockSensorJobs()
                .stream().forEach(mockSensorJob -> {
            try {
                mockSensorJob.terminate();
            } catch (Exception e) {
                log.error("SensorJob {"+mockSensorJob.getMockSensor().getId()+"} could not be terminated successfully");
            }
        });

    }

    @Override
    public boolean isStartedProducing() {
        return startedProducing;
    }


    public List<MockSensorPrototypeJob> createAndProcessMockSensorPrototypeJobsFromMockSensorPrototypes() throws Exception{
        log.debug("Entered createAndProcessMockSensorPrototypeJobs()");
        String errorMessage;
        List<MockSensorPrototypeJob> mockSensorPrototypeJobs;
        try {
            mockSensorPrototypeJobs = mockSensorPrototypes.stream()
                    .map(sensorPrototype -> new MockSensorPrototypeJob(sensorPrototype))
                    .collect(Collectors.toList());
        }catch (Exception  exception) {
            errorMessage = "Couldn't create MockSensorPrototypeJobs from mockSensorPrototypes:" +exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage);
        }
        if(mockSensorPrototypeJobs == null || mockSensorPrototypeJobs.isEmpty()){
            errorMessage = "MockSensorPrototypeJobs could not be created.Please Check logs or verify that file has valid sensorsPrototypes";
            throw new Exception(errorMessage);
        }
        try{
            for(MockSensorPrototypeJob mockSensorPrototypeJob : mockSensorPrototypeJobs)
                SensorUtils.createSensorPrototypeMessagePrototype(mockSensorPrototypeJob.getMockSensorPrototype());
        }catch (Exception  exception) {
            errorMessage = "Failure while processing MockSensorPrototypeJobs from mockSensorPrototypes: "+exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage);
        }
        return mockSensorPrototypeJobs;
    }


    public void createAndProcessOutputWriterThread(List<MockSensorPrototypeJob> mockSensorPrototypeJobs){
        log.debug("Entered createOutputWriterThread()");
        Optional<Boolean> createWriterThreadOptional = mockSensorPrototypeJobs.stream()
                .filter(mockSensorPrototypeJob -> mockSensorPrototypeJob.getMockSensorPrototype().getOutputFile() != null)
                .findAny()
                .map(mockSensorPrototypeJob -> true);
        if(createWriterThreadOptional.isPresent() && createWriterThreadOptional.get()){
            log.debug("Will create writerThread");
            workloadGenerator.setWriteToOutputFile(true);
            WriterThread writerThread = new WriterThread();
            workloadGenerator.setWriterThread(writerThread);
            workloadGenerator.getWriterThread().start();
        }
        else {
            workloadGenerator.setWriteToOutputFile(false);
        }
        mockSensorPrototypeJobs.stream()
                .filter(mockSensorPrototypeJob -> mockSensorPrototypeJob.getMockSensorPrototype().getOutputFile() != null)
                .forEach(mockSensorPrototypeJob -> {
                    try{
                        workloadGenerator.getWriterThread().addMockSensorPrototypeOutputFileInfo(mockSensorPrototypeJob.getMockSensorPrototype());}
                    catch (Exception exception){
                        log.error(EXCEPTION_CAUGHT+"while creating fileWriter for output file for mockSensorPrototype {"+mockSensorPrototypeJob.getMockSensorPrototypeName()+"}. Will not export to outpuf file for this mockSensorPortotype.",exception);
                    }}
                );
    }


    public List<MockSensor> createMockSensors(List<MockSensorPrototypeJob> mockSensorPrototypeJobs) throws Exception{
        log.debug("Entered createMockSensors()");
        String errorMessage = "";
        try{
            return mockSensorPrototypeJobs.stream()
                    .map(mockSensorPrototypeJob -> mockSensorPrototypeJob.createMockSensors())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }catch (Exception exception){
            errorMessage = "Couldn't create mockSensors from mockSensorPrototypes: " + exception.getMessage();
            log.error(errorMessage , exception);
            throw new Exception(errorMessage);
        }
    }


    private List<Scenario> getValidScenarios(MockSensorPrototype mockSensorPrototype){
        List<Scenario> validScenarios = new ArrayList<>();
        for(Scenario scenario: mockSensorPrototype.getScenarios()){
            if(scenario.getScenarioName() == null){
                log.warn("Found scenario without Name. Scenario will be ignored. Scenario Name field is mandatory");
                continue;
            }
            if(scenario.getScenarioDelay() == null || scenario.getScenarioDuration() == null){
                log.warn("Delay or duration for scenario {"+scenario.getScenarioName()+"} was not provided. Scenario will be ignored.");
                continue;
            }
            if(scenario.getScenarioDelay() < 0 || scenario.getScenarioDuration() < 0){
                log.warn("Delay or duration for scenario {"+scenario.getScenarioName()+"} was negative. Scenario will be ignored.");
                continue;
            }
            if(scenario.getScenarioFieldValueInfoList() ==  null || scenario.getScenarioFieldValueInfoList().isEmpty()){
                log.warn("ScenarioFieldValueInfoList for scenario {"+scenario.getScenarioName()+"} is empty or not provided. Scenario will be ignored.");
                continue;
            }
            List<ScenarioFieldValueInfo> validScenarioFieldValueInfoList = getValidScenarioFieldValueInfoForMockSensorPrototype(scenario.getScenarioName(), scenario.getScenarioFieldValueInfoList(), mockSensorPrototype.getMessagePrototype().getFieldsPrototypes());
            scenario.setScenarioFieldValueInfoList(validScenarioFieldValueInfoList);
            try{
                String sensorId = validateAndProcessScenarioSensorId(scenario, mockSensorPrototype.getSensorPrototypeName(), mockSensorPrototype.getSensorsQuantity());
                scenario.setSensorId(sensorId);
                validScenarios.add(scenario);
            }catch (ValidationException validationException){
                log.warn(validationException.getMessage());
                continue;
            }
        }
        return validScenarios;
    }


    public List<ScenarioFieldValueInfo> getValidScenarioFieldValueInfoForMockSensorPrototype(String scenarioName, List<ScenarioFieldValueInfo> scenarioFieldValueInfoList, List<FieldPrototype> sensorPrototypeFieldPrototypes){
        List<ScenarioFieldValueInfo> validScenarioFieldValueInfoList = new ArrayList<>();
        HashSet<String> fieldNameSet = new HashSet<>();
        sensorPrototypeFieldPrototypes.stream().
                forEach(field -> fieldNameSet.add(field.getName()));

        for(ScenarioFieldValueInfo scenarioFieldValueInfo: scenarioFieldValueInfoList){
            if(fieldNameSet.contains(scenarioFieldValueInfo.getSensorFieldScenarioName())){
                try{
                    GenerationRateWrapper generationRateWrapper = GenerationRatesUtils.processGenerationRate(scenarioFieldValueInfo.getSensorFieldScenarioGenerationRate());
                    scenarioFieldValueInfo.setSensorFieldScenarioGenerationRate(generationRateWrapper.getGenerationRate());
                    scenarioFieldValueInfo.setSensorFieldScenarioGenerationRateEnum(generationRateWrapper.getGenerationRateEnum());
                }catch (Exception exception){
                    log.error("Could not process generationRate for field {"+scenarioFieldValueInfo.getSensorFieldScenarioName()+"} and scenario {"+scenarioName+"} due to "+exception.getMessage()+".Scenario field will be ignored.");
                }
                validScenarioFieldValueInfoList.add(scenarioFieldValueInfo);
            }
            else{
                log.error("Could not find scenario field {"+scenarioFieldValueInfo.getSensorFieldScenarioName()+"} for scenario {"+scenarioName+"} for the sensorPrototype .Scenario field will be ignored.");
            }
        }

        return validScenarioFieldValueInfoList;
    }


    public String validateAndProcessScenarioSensorId(Scenario scenario, String mockSensorPrototypeName, int mockSensorPrototypeQuantity) throws ValidationException {
        if(scenario.getSensorId() ==  null){
            throw new ValidationException("SensorId for scenario {"+scenario.getScenarioName()+"} was not provided. Scenario will be ignored.");
        }
        Integer sensorId = null;
        try {
            sensorId = Integer.parseInt(scenario.getSensorId());
        }catch (Exception e){
            throw new ValidationException("SensorId for scenario {"+scenario.getScenarioName()+"} was not numeric. Scenario will be ignored.");
        }
        if(sensorId > mockSensorPrototypeQuantity){
            throw new ValidationException("SensorId {"+sensorId+"} for scenario {"+scenario.getScenarioName()+"} is out of range of the mock Sensors Quantity for this mockSensorPrototype. Scenario will be ignored.");
        }
        return mockSensorPrototypeName + "_" + sensorId;
    }


    public void processAndScheduleScenarios(List<MockSensorJob> mockSensorJobs) {
        log.debug("Entered processAndScheduleScenarios()");
        List<Scenario> validScenarios = mockSensorPrototypes.stream()
                .filter(mockSensorPrototype -> mockSensorPrototype.getScenarios() != null && !mockSensorPrototype.getScenarios().isEmpty())
                .map(mockSensorPrototype -> getValidScenarios(mockSensorPrototype))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (!validScenarios.isEmpty()) {
            log.info("Valid scenarios found!");
            scenarioManager = new ScenarioManager(validScenarios, mockSensorJobs);
        }
    }


    public CopyOnWriteArrayList<MockSensorJob> createAndStartMockSensorJobs() throws Exception{
        log.debug("Entered createAndStartMockSensorJobs()");
        String errorMessage = "";
        log.info("Creating {" + mockSensorService.getAllMockSensors().size() + "} mock Sensors....");
        CopyOnWriteArrayList<MockSensorJob> mockSensorJobs = new CopyOnWriteArrayList<>();
        try {
            mockSensorService.getAllMockSensors().parallelStream()
                    .map(mockSensor -> new MockSensorJob(mockSensor))
                    .forEach(mockSensorJob -> {
                        if (mockSensorJob.getMockSensor().getMockSensorPrototype().getOutputFile() != null) {
                            mockSensorJob.setWriterThread(workloadGenerator.getWriterThread());
                            mockSensorJob.setWriteTofFile(true);
                        } else
                            mockSensorJob.setWriteTofFile(false);
                        mockSensorJob.setSensorMessageSendService(sensorMessageSendService);
                        mockSensorJobs.add(mockSensorJob);
                    });
        }catch (Exception exception){
            errorMessage = EXCEPTION_CAUGHT_WHILE + "while creating mock Sensor Threads:" + exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage);
        }
        try {
            mockSensorJobs.stream().
                    forEach(mockSensorJob -> mockSensorJob.start());
        }catch (Exception exception){
            errorMessage = EXCEPTION_CAUGHT_WHILE + "while starting mock Sensor Threads:" + exception.getMessage();
            log.error(errorMessage, exception);
            throw new Exception(errorMessage);
        }
        return mockSensorJobs;
    }



    public synchronized void terminateWriterThread()throws Exception{
        if(workloadGenerator.isWriteToOutputFile()){
            while(workloadGenerator.getWriterThread().getBlockingDeque().size()!=0);
            workloadGenerator.getWriterThread().terminate();}
    }


    public synchronized void cancelAllMockSensorJobs()throws  Exception {

        this.workloadGenerator.getMockSensorJobs().stream().
                forEach(mockSensorJob -> {try {
                    mockSensorJob.pause();
                }catch (Exception runtimeException){}
                });

    }

    public synchronized void createMockSensorJobsForMockSensorPrototype(Exchange exchange,String  mockSensorPrototypeName,int quantity){
        boolean found = false;
        for(MockSensorPrototypeJob mockSensorPrototypeJob :workloadGenerator.getMockSensorPrototypeJobs()){
            if(mockSensorPrototypeJob.getMockSensorPrototypeName().equals(mockSensorPrototypeName)){
                found = true;
                for (int i = 0; i < quantity; ++i) {
                    MockSensor newMockSensor = new MockSensor(mockSensorPrototypeJob.getMockSensorPrototype(), mockSensorPrototypeJob.getCounter().get());
                    MockSensorJob newMockSensorJob = new MockSensorJob(newMockSensor);
                    if (workloadGenerator.isWriteToOutputFile()) {
                        newMockSensorJob.setWriterThread(workloadGenerator.getWriterThread());
                        newMockSensorJob.setWriteTofFile(workloadGenerator.isWriteToOutputFile());
                    }
                    newMockSensorJob.setSensorMessageSendService(sensorMessageSendService);
                    workloadGenerator.getMockSensorJobs().add(newMockSensorJob);
                    newMockSensorJob.start();
                    mockSensorPrototypeJob.getCounter().getAndIncrement();
                    mockSensorPrototypeJob.getSensorsNumber().getAndIncrement();
                    mockSensorPrototypeService.editMockSensorsNumberForMockSensorPrototype(mockSensorPrototypeJob.getSensorsNumber().get(),mockSensorPrototypeName);
                    mockSensorService.addMockSensor(newMockSensor);
                }
                break;
            }
        }
        if(!found){
            exchange.setProperty(ERROR_MESSAGE, "mockSensorPrototypeName with name: "+mockSensorPrototypeName+ "not found");
            exchange.setProperty(ERROR_MESSAGE_TYPE,VALIDATION_ERROR);
            exchange.setHttpStatus(HTTP_NOT_FOUND);
        }
        else
            exchange.setHttpStatus(HTTP_CREATED);
    }


    public synchronized void createMockSensorJobsForNewSensorPrototype(Exchange exchange, MockSensorPrototype mockSensorPrototype){
        MockSensorPrototype mockSensorPrototype1 = mockSensorPrototypeService.retrieveMockSensorPrototypeByName(mockSensorPrototype.getSensorPrototypeName());

        if (mockSensorPrototype1!=null) {//EXIST ALREADY
            exchange.setProperty(ERROR_MESSAGE, "MockSensorPrototype with name: " + mockSensorPrototype.getSensorPrototypeName() + " already exists!");
            exchange.setProperty(ERROR_MESSAGE_TYPE, VALIDATION_ERROR);
            exchange.setHttpStatus(HTTP_BAD_REQUEST);
        } else {//NEW
            try {
                SensorUtils.createSensorPrototypeMessagePrototype(mockSensorPrototype);
                MockSensorPrototypeJob newMockSensorPrototypeJob = new MockSensorPrototypeJob(mockSensorPrototype);
                List<MockSensor> newMockSensorsList = newMockSensorPrototypeJob.createMockSensors();
                mockSensorService.addAllMockSensors(newMockSensorsList);
                mockSensorPrototypeService.addMockSensorPrototype(mockSensorPrototype);
                workloadGenerator.getMockSensorPrototypeJobs().add(newMockSensorPrototypeJob);
                newMockSensorsList.stream()
                        .map(mockSensor -> new MockSensorJob(mockSensor))
                        .forEach(mockSensorJob -> {
                                    if (workloadGenerator.isWriteToOutputFile()) {
                                        mockSensorJob.setWriterThread(workloadGenerator.getWriterThread());
                                        mockSensorJob.setWriteTofFile(workloadGenerator.isWriteToOutputFile());
                                    }
                                    mockSensorJob.setSensorMessageSendService(sensorMessageSendService);
                                    mockSensorJob.start();
                                    workloadGenerator.getMockSensorJobs().add(mockSensorJob);
                                }
                        );
                exchange.setHttpStatus(HTTP_CREATED);
            } catch (Exception e) {
                String errorMessage = "Couldn't initialize mockSensors from MockSensorPrototype due to:" + e.getMessage();
                log.error(EXCEPTION_CAUGHT + errorMessage, e);
                exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                exchange.setProperty(ERROR_MESSAGE, errorMessage);
                exchange.setProperty(ERROR_MESSAGE_TYPE, INTERNAL_SERVER_ERROR);
            }
        }
    }



    public synchronized void terminateMockSensorJob(Exchange exchange,String mockSensorId){
        boolean found = false;
        for(int i = 0; i < workloadGenerator.getMockSensorJobs().size(); ++i){
            MockSensorJob mockSensorJob =workloadGenerator.getMockSensorJobs().get(i);
            if (mockSensorJob.getMockSensor().getId().equals(mockSensorId)) {
                found = true;
                try {
                    mockSensorJob.terminate();
                } catch (Exception e) {
                    log.error("SensorJob could not be terminated: "+e.getMessage());
                }
                for (int j = 0; j < workloadGenerator.getMockSensorPrototypeJobs().size(); ++j) {
                    if (workloadGenerator.getMockSensorPrototypeJobs().get(j).getMockSensorPrototypeName().equals(mockSensorJob.getMockSensor().getMockSensorPrototype().getSensorPrototypeName())) {
                        MockSensorPrototypeJob mockSensorPrototypeJob = workloadGenerator.getMockSensorPrototypeJobs().get(j);
                        mockSensorPrototypeJob.getSensorsNumber().getAndDecrement();
                        mockSensorPrototypeService.editMockSensorsNumberForMockSensorPrototype(mockSensorPrototypeJob.getSensorsNumber().get(), mockSensorPrototypeJob.getMockSensorPrototypeName());
                        break;
                    }
                }
                workloadGenerator.getMockSensorJobs().remove(i);
                break;
            }
            mockSensorService.deleteMockSensor(mockSensorId);
        }
        if(found){
            exchange.setHttpStatus(HTTP_NO_CONTENT);
        }
        else{//NOT FOUND
            exchange.setProperty(ERROR_MESSAGE,"MockSensor with id: "+mockSensorId+ " not found");
            exchange.setProperty(ERROR_MESSAGE_TYPE,VALIDATION_ERROR);
            exchange.setHttpStatus(HTTP_NOT_FOUND);
        }
    }


    public synchronized void terminateMockSensorJobsForMockSensorPrototype(Exchange exchange, String mockSensorPrototypeName){
        boolean found = false;
        for(int i = 0; i < workloadGenerator.getMockSensorPrototypeJobs().size(); ++i) {
            if(workloadGenerator.getMockSensorPrototypeJobs().get(i).getMockSensorPrototypeName().equals(mockSensorPrototypeName)){
                found = true;
                workloadGenerator.getMockSensorPrototypeJobs().remove(i);
                break;}
        }
        if(!found){
            //NOT FOUND
            exchange.setProperty(ERROR_MESSAGE,"MockSensorPrototype with name: " + mockSensorPrototypeName + " not found");
            exchange.setProperty(ERROR_MESSAGE_TYPE,VALIDATION_ERROR);
            exchange.setHttpStatus(HTTP_NOT_FOUND);
            return;
        }
        mockSensorService.deleteMockSensorsOfMockSensorPrototype(mockSensorPrototypeName);
        mockSensorPrototypeService.deleteMockSensorPrototype(mockSensorPrototypeName);
        ListIterator<MockSensorJob> mockSensorJobListIterator = workloadGenerator.getMockSensorJobs().listIterator();
        while (mockSensorJobListIterator.hasNext()) {
            MockSensorJob mockSensorJob = mockSensorJobListIterator.next();
            if(mockSensorJob.getMockSensor().getMockSensorPrototype().getSensorPrototypeName().equals(mockSensorPrototypeName)){
                try {
                    mockSensorJob.terminate();
                } catch (Exception exception) {
                    log.error("SensorJob {"+mockSensorJob.getMockSensor().getId()+"} could not be terminated succesfully due to:"+exception.getMessage(), exception);
                }
                mockSensorJobListIterator.remove();
            }
        }
        exchange.setHttpStatus(HTTP_NO_CONTENT);
    }


    public synchronized void clearMockSensorRepository(){
        mockSensorService.deleteAll();
    }


    public synchronized void clearMockSensorPrototypeRepository(){
        mockSensorPrototypeService.deleteAll();
    }

}
