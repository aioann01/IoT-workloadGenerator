package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.castValue;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.SensorUtils.isValueOfType;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstantGenerationRate extends GenerationRate {

    private Object value;

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

    @Override
    public Object generateValue(TypesEnum type) {
        return castValue(value, type);
    }

    @Override
    public void processAndValidate(TypesEnum type) throws ValidationException {
        if(value == null || !isValueOfType(type, value))
            throw new ValidationException("toDo");
    }
}
