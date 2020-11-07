package cy.cs.ucy.ade.aioann01.Sensors.Model.Http;

public class ValidationException extends Exception{


    public ValidationException(String errorMessage){
        super(errorMessage);
    }
}
