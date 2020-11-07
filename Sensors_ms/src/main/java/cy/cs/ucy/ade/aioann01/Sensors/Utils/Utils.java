package cy.cs.ucy.ade.aioann01.Sensors.Utils;


import cy.cs.ucy.ade.aioann01.Sensors.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.ValidationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;



import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.*;

public class Utils<T> {
    private static Properties properties;
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static String readPropertyFromConfigs(String propertyName) throws Exception {
        if (properties == null) {
            try {
                properties = new Properties();
                InputStream is=Utils.class.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES);
                properties.load(is);
            } catch (Exception e) {
                throw new Exception("application properties file does not exist in resources");
            }
        }
        try {
            return properties.getProperty(propertyName);
        } catch (Exception e) {
            throw new Exception("Property {" + propertyName + "} could not be read from application.json");
        }

    }

    public static void setExchangeFromHttpClientErrorException(Exchange exchange, Exception exception){
        String errorMessage=null;
        if(exception instanceof HttpClientErrorException){
            HttpClientErrorException httpClientErrorException=(HttpClientErrorException)exception;
            exchange.setHttpStatus(httpClientErrorException.getStatusCode());
            try{
                JSONObject jsonObject=new JSONObject(httpClientErrorException.getResponseBodyAsString());
                if(jsonObject.has("message"))
                    errorMessage=jsonObject.get("message").toString();
                else
                    errorMessage=httpClientErrorException.getResponseBodyAsString();}
            catch (JSONException e) {
                errorMessage=httpClientErrorException.getResponseBodyAsString();
            }
        }
        else if(exception instanceof HttpServerErrorException) {
            HttpServerErrorException httpServerErrorException = (HttpServerErrorException) exception;
            exchange.setHttpStatus(httpServerErrorException.getStatusCode());
            try {
                JSONObject jsonObject = new JSONObject(httpServerErrorException.getResponseBodyAsString());
                if (jsonObject.has("message"))
                    errorMessage = jsonObject.get("message").toString();
                else
                    errorMessage = httpServerErrorException.getResponseBodyAsString();
            } catch (JSONException e) {
                errorMessage = httpServerErrorException.getResponseBodyAsString();
            }
        }
        else{
            exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
            if(exception.getCause()==null)
                errorMessage=exception.getMessage();
            else
                errorMessage=exception.getCause().getMessage()+" to WorkloadGenerator";
        }
        exchange.setProperty(EXCEPTION_IS_SET,true);
        exchange.setProperty(ERROR_MESSAGE,errorMessage);
    }

    public static String classType(TypesEnum typesEnum){
        switch (typesEnum){
            case DOUBLE:return Double.class.getName();
            case STRING:return String.class.getName();
            case BOOLEAN:return Boolean.class.getName();
            case INTEGER:return Integer.class.getName();
            default:return null;
        }
    }


    public static void buildValidationException(Exchange exchange,String errorMessage,String errorMessageType)throws  ValidationException{
        exchange.setProperty(ERROR_MESSAGE,errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE,errorMessageType==null?VALIDATION_ERROR:errorMessageType);
        exchange.setHttpStatus(HTTP_BAD_REQUEST);
        exchange.setProperty(EXCEPTION_IS_SET,true);
        throw new ValidationException(errorMessage);
    };


    public static void buildException(Exchange exchange,String errorMessage,String errorMessageType)throws Exception{
        exchange.setProperty(ERROR_MESSAGE,errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE,errorMessageType==null?UNEXPECTED_ERROR_OCCURRED:errorMessageType);
        exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        exchange.setProperty(EXCEPTION_IS_SET,true);
        throw new Exception(errorMessage);
    };

}



