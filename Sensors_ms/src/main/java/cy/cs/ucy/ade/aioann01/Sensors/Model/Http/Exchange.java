package cy.cs.ucy.ade.aioann01.Sensors.Model.Http;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.HTTP_SUCCESS;

public class Exchange<T> {


    private Exception exception;

    private int responseCode;

    private HashMap<String,Object> properties;

    private HashMap<String,Object> headers;

    private HttpStatus httpStatus;

    private T body;
    //public Exchange(){}
    public Exchange() {
        this.exception = null;
        this.responseCode = 0;
        this.properties = new HashMap<>();
        this.headers = new HashMap<>();
        this.httpStatus = HTTP_SUCCESS;
        this.body = null;
    }

    public void addHeader(String name, Object value){
        headers.put(name,value);
    }
    public void removeHeader(String name){
        headers.remove(name);
    }

    public void setProperty(String name,Object value){
        properties.put(name,value);
    }
    public Object getProperty(String name){
        return properties.get(name);}

    public <T>T getProperty(String name,Class<T> type) {
        Object property=getProperty(name);
        if(property==null)
            return null;
        else if (type.isInstance(property))
            return (T)(property);
        else return  null;
    }


    public void removeProperty(String name){
        properties.remove(name);
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
