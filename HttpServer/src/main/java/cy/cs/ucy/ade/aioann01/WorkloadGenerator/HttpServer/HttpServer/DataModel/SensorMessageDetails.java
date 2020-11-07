package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer.DataModel;

import java.util.Date;

public class SensorMessageDetails {

    private String messageDate;

    private String message;

    private String contentType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
