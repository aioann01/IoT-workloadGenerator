package cy.cs.ucy.ade.aioann01.SSO.Model.Http;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"status","message"})
public class SuccessResponseMessage extends ResponseMessage{
    private String status="Success";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessResponseMessage(String message) {
        super(message);
    }
}
