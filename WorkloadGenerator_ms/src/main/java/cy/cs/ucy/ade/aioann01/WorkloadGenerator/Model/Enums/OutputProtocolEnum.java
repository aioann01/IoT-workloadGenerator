package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums;

import org.apache.tomcat.util.buf.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public enum OutputProtocolEnum {
    HTTP ("HTTP"),
    MQTT ("MQTT"),
    KAFKA("KAFKA");


    private static Map<String,OutputProtocolEnum> enumMap;

    static {
        enumMap = new HashMap<>();
        Arrays.stream(values())
                .forEach(enumeration -> enumMap.put(enumeration.getValue(), enumeration));
    }
    private final String value;

    OutputProtocolEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value.toLowerCase();
    }

    public static OutputProtocolEnum getEnumByValue(String value) {
        return enumMap.get(value.toLowerCase());
    }

    public static String getSupportedProtocols(){
        return "["+ StringUtils.join(getNames(OutputProtocolEnum.class),',') +"]";
    }

    public static List<String> getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
