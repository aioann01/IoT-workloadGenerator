package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;

public class GenerationRateWrapper {

    private GenerationRate generationRate;

    private GenerationRateEnum generationRateEnum;

    public GenerationRateWrapper() {
    }

    public GenerationRateWrapper(GenerationRate generationRate, GenerationRateEnum generationRateEnum) {
        this.generationRate = generationRate;
        this.generationRateEnum = generationRateEnum;
    }

    public GenerationRate getGenerationRate() {
        return generationRate;
    }

    public void setGenerationRate(GenerationRate generationRate) {
        this.generationRate = generationRate;
    }

    public GenerationRateEnum getGenerationRateEnum() {
        return generationRateEnum;
    }

    public void setGenerationRateEnum(GenerationRateEnum generationRateEnum) {
        this.generationRateEnum = generationRateEnum;
    }
}
