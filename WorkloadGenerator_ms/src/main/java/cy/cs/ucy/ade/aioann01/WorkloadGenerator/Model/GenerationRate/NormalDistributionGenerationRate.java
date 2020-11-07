package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NormalDistributionGenerationRate extends GenerationRate{

    private Number mean;

    private Number deviation;

    public Number getMean() {
        return mean;
    }

    public void setMean(Number mean) {
        this.mean = mean;
    }

    public Number getDeviation() {
        return deviation;
    }

    public void setDeviation(Number deviation) {
        this.deviation = deviation;
    }

    @Override
    public String toString() {
        return "\"normalDistribution\":{" +
                "\"mean\":" + mean +
                ", \"deviation\":" + deviation +
                '}';
    }
}
