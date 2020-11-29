package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.MILISECONDS_TO_SECONDS;

public class ScenarioJob extends TimerTask{

    private Scenario scenario;

    private MockSensorJob mockSensorJob;


    private static final Logger log = LoggerFactory.getLogger(ScenarioJob.class);

    public ScenarioJob(Scenario scenario, MockSensorJob mockSensorJob){
        this.mockSensorJob = mockSensorJob;
        this.scenario = scenario;
        log.info("Scenario {"+scenario.getScenarioName()+"} is scheduled");
    }

    public synchronized void cancelScenario(){
        //scenarioTask.interrupt();
        // scenarioTask.notify();
//        timer.cancel();
//        timer.purge();
        this.notify();
    try {
        Thread.interrupted();
    }catch (Exception e) {
        log.error("irtas",e);
    }
    }

    @Override
    public void run(){
        try{
            log.info("Scenario {"+scenario.getScenarioName()+"} started");
            mockSensorJob.triggerScenario(scenario);
           synchronized (this){
               wait(scenario.getScenarioDuration() * MILISECONDS_TO_SECONDS);
           }
            mockSensorJob.terminateScenario();
            log.info("Scenario {"+ scenario.getScenarioName()+ "} was terminated");
            //   timer.cancel();
        }catch(InterruptedException interruptedException){
            log.error("Scenario {"+ scenario.getScenarioName()+ "} was interrupted");
            //   timer.cancel();
        }
        catch (Exception e){
            log.error("Scenario {"+ scenario.getScenarioName()+ "} was interrupted");
            //    timer.cancel();
        }
    }

}
