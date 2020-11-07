package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistributionGenerationRate extends GenerationRate{

    List<Distribution> distributions;

    public DistributionGenerationRate(){}

    public  DistributionGenerationRate(List<Distribution> distributions){
        this.distributions = distributions;
    }

    public List<Distribution> getDistributions() {
        return distributions;
    }

}
