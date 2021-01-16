package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.Utils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstantGenerationRate extends GenerationRate {

    private  Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ConstantGenerationRate(Object value){
        this.value = value;
    }

    public ConstantGenerationRate(){}


    @Override
    public String toString() {
        return "\"constant\":{" +
                "\"value\":" + value +
                '}';
    }
}
