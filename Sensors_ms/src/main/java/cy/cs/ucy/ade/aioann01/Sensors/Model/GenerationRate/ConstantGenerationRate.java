package cy.cs.ucy.ade.aioann01.Sensors.Model.GenerationRate;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Enums.TypesEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstantGenerationRate extends GenerationRate {

    private  Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object generateMessage(TypesEnum type) {
      //  String className= Utils.classType(type);

        return value;    }
}
