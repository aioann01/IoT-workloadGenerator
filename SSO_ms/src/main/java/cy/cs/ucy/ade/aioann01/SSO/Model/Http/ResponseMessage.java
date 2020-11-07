package cy.cs.ucy.ade.aioann01.SSO.Model.Http;

public class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
