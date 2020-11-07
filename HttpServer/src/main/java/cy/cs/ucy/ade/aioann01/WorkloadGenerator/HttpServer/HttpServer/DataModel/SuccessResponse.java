package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessResponse {
@JsonProperty("message")
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }


    public SuccessResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
