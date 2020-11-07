package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;


import com.fasterxml.jackson.annotation.JsonInclude;

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
