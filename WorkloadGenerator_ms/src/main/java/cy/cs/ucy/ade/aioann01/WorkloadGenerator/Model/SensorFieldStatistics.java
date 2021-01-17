package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;

import java.util.List;

public class SensorFieldStatistics {

    private Boolean isNumber;

    private Number numberValue;

    private Number minValue;

    private Number maxValue;

    private Double sum;

    private TypesEnum type;

    private Integer falseCount;

    private Integer trueCount;

    private String fieldName;

    private int sumOfGenerationRate;

    private List<Number> sampleValues;

    private int samplesCount;

    public int getSumOfGenerationRate() {
        return sumOfGenerationRate;
    }

    public void setSumOfGenerationRate(int sumOfGenerationRate) {
        this.sumOfGenerationRate = sumOfGenerationRate;
    }

    public SensorFieldStatistics(){
        this.samplesCount = 0;;
    }

    public String toCSVRecordString(){
        return fieldName + ","
                + type + ","
                + (type.equals(TypesEnum.INTEGER) || type.equals(TypesEnum.DOUBLE) ? sum : "" ) + ","
                + (type.equals(TypesEnum.INTEGER) || type.equals(TypesEnum.DOUBLE) ? minValue : "" ) + ","
                + (type.equals(TypesEnum.INTEGER) || type.equals(TypesEnum.DOUBLE) ? maxValue : "" ) + ","
                + (type.equals(TypesEnum.BOOLEAN) ? trueCount : "" ) + ","
                + (type.equals(TypesEnum.BOOLEAN) ? falseCount : "" ) + "";
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getNumber() {
        return isNumber;
    }

    public void setNumber(Boolean number) {
        isNumber = number;
    }

    public Number getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Number numberValue) {
        this.numberValue = numberValue;
    }

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue = minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public TypesEnum getType() {
        return type;
    }

    public void setType(TypesEnum type) {
        this.type = type;
    }

    public Integer getFalseCount() {
        return falseCount;
    }

    public void setFalseCount(Integer falseCount) {
        this.falseCount = falseCount;
    }

    public Integer getTrueCount() {
        return trueCount;
    }

    public void setTrueCount(Integer trueCount) {
        this.trueCount = trueCount;
    }

    public List<Number> getSampleValues() {
        return sampleValues;
    }

    public void setSampleValues(List<Number> sampleValues) {
        this.sampleValues = sampleValues;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
        this.samplesCount = samplesCount;
    }
}
