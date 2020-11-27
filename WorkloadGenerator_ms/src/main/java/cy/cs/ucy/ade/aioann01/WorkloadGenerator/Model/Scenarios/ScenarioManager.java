package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Scenarios;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Thread.MockSensorJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.MILISECONDS_TO_SECONDS;

public class ScenarioManager {

    List<ScenarioJob> scenarioJobs;

    private Timer timer;


    public ScenarioManager(List<Scenario> scenarios, List<MockSensorJob> mockSensorJobs){
       timer = new Timer();
       scenarioJobs = new ArrayList<>();
        for(Scenario scenario: scenarios){
            for(MockSensorJob mockSensorJob: mockSensorJobs){
                if(mockSensorJob.getSensorId().equals(scenario.getSensorId())){
                    ScenarioJob scenarioJob = new ScenarioJob(scenario, mockSensorJob);
                    scenarioJobs.add(scenarioJob);
                    timer.schedule(scenarioJob,scenario.getScenarioDelay() * MILISECONDS_TO_SECONDS);
                    break;}
            }
        }
    }

    public void cancelScenarioJobs(){
        scenarioJobs.stream().forEach(scenarioJob -> scenarioJob.cancelScenario());
        scenarioJobs.clear();
        timer.cancel();
    };
}
