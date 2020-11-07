package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http;

public class ValidationException extends Exception{

    public ValidationException(String errorMessage){
        super(errorMessage);
    }
}
