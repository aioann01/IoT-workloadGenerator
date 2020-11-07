package cy.cs.ucy.ade.aioann01.Sensors.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Enums.TypesEnum;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistributionGenerationRate extends GenerationRate{

   List<Distribution> distributions;


    public List<Distribution> getDistributions() {
        return distributions;
    }

    @Override
    public Object generateMessage(TypesEnum type) {
        return null;
    }
}
