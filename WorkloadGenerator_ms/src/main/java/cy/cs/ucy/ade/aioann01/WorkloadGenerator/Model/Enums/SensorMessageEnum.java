package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums;

import javax.xml.bind.Element;

public enum SensorMessageEnum {
    JSON ("json"),
    XML ("xml"),
    TEXT("txt");


    public final String value;

    SensorMessageEnum(String value){
        this.value = value;
    }
}
