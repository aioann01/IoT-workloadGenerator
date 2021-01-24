package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.GenerationRate;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;

public abstract class GenerationRate {

    public abstract Object generateValue(TypesEnum type);

    public abstract void processAndValidate(TypesEnum type) throws ValidationException;

}
