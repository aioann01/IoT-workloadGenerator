package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.GenerationRateEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldPrototype<T> {
    private String name;

    private T value;

    private  String type;

    private TypesEnum typeEnum;

    private GenerationRateEnum generationRateEnum;

    private String unit;

    public FieldPrototype() {
    }

    public FieldPrototype(String name, T value, String type, TypesEnum typeEnum, GenerationRateEnum generationRateEnum, String unit) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.typeEnum = typeEnum;
        this.generationRateEnum = generationRateEnum;
        this.unit = unit;
    }

    public TypesEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(TypesEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public GenerationRateEnum getGenerationRateEnum() {
        return generationRateEnum;
    }

    public void setGenerationRateEnum(GenerationRateEnum generationRateEnum) {
        this.generationRateEnum = generationRateEnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


}
