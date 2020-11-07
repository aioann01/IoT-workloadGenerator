package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.FieldPrototype;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.SensorFieldStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Properties;


import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum.INTEGER;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
@Component
public class Utils<T> {

    //private static Properties applicationProperties;
    private static final Logger log = LoggerFactory.getLogger(Utils.class);



    //static {
//        try {
//            applicationProperties = new Properties();
//            InputStream is = Utils.class.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES);
//            applicationProperties.load(is);
//
//        } catch (Exception e) {
//            log.error("Could not load application.properties", e);
//        }
//        try {
//            configsDirectory = readApplicationProperty(CONFIGS_DIRECTORY_PROPERTY);
//        } catch (Exception exception) {
//            log.error("Could not find {configs.directory} property in application.properties. Please provide the property for the system to know from where to retrieve configs files", exception);
//        }

  //  }

//    //Application.properties
//    public static String readApplicationProperty(String propertyName) throws Exception {
//        String errorMessage = null;
//        if (applicationProperties == null) {
//            try {
//                applicationProperties = new Properties();
//                InputStream is = Utils.class.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES);
//                applicationProperties.load(is);
//            } catch (Exception e) {
//                errorMessage = "Could not load application.properties:" + e.getMessage();
//                log.error(errorMessage);
//                log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
//                throw new Exception(errorMessage);
//            }
//        }
//        try {
//            return applicationProperties.getProperty(propertyName);
//        } catch (Exception e) {
//            errorMessage = "Property {" + propertyName + "} could not be read from application.json";
//            log.error(errorMessage);
//            log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
//            throw new Exception(errorMessage);
//        }
//    }

//    //WorkloadGenerator.properties
//    public static String readPropertyFromConfigs(String propertyName) throws Exception {
//        String errorMessage = null;
////        if (configsDirectory == null)
////            configsDirectory = readApplicationProperty(CONFIGS_DIRECTORY_PROPERTY);
//        if (workloadGeneratorProperties == null) {
//            try {
//                workloadGeneratorProperties = new Properties();
//                //Container
//                workloadGeneratorProperties.load(new FileInputStream(configsDirectory + WORKLOAD_GENERATOR_PROPERTIES_WINDOWS));
//
//                //local
//                // properties.load(new FileInputStream("/home/configs/"+WORKLOAD_GENERATOR_PROPERTIES_WINDOWS));
//
//            } catch (Exception e) {
//                errorMessage = WORKLOAD_GENERATOR_PROPERTIES_WINDOWS + "file does not exist in " + configsDirectory;
//                log.error(errorMessage);
//                log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
//                throw new Exception(errorMessage);
//            }
//        }
//        try {
//            return workloadGeneratorProperties.getProperty(propertyName);
//        } catch (Exception e) {
//            errorMessage = "Property {" + propertyName + "} could not be read from application.json";
//            log.error(errorMessage);
//            log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
//            throw new Exception(errorMessage);
//        }
//
//    }


    public static void setExchangeFromHttpClientErrorException(Exchange exchange, HttpClientErrorException httpClientErrorException) {
        HttpStatus requestExcpetionStatus = httpClientErrorException.getStatusCode();
        exchange.setProperty(ERROR_MESSAGE, httpClientErrorException.getMessage());
        if (requestExcpetionStatus != null) {
            switch (requestExcpetionStatus) {
                case NOT_FOUND:
                case BAD_REQUEST:
                    exchange.setHttpStatus(requestExcpetionStatus);
                    break;
                case INTERNAL_SERVER_ERROR:
                    exchange.setProperty(ERROR_MESSAGE_TYPE, INTERNAL_SERVER_ERROR);
                    exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
                    break;
                default:
                    exchange.setHttpStatus(requestExcpetionStatus);
            }
        } else {
            exchange.setProperty(ERROR_MESSAGE_TYPE, UNEXPECTED_ERROR_OCCURRED);
            exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    public static String classType(TypesEnum typesEnum) {
        switch (typesEnum) {
            case DOUBLE:
                return Double.class.getName();
            case STRING:
                return String.class.getName();
            case BOOLEAN:
                return Boolean.class.getName();
            case INTEGER:
                return Integer.class.getName();
            default:
                return null;
        }
    }


    public static void buildValidationException(Exchange exchange, String errorMessage, String errorMessageType) throws ValidationException {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null ? VALIDATION_ERROR : errorMessageType);
        exchange.setHttpStatus(HTTP_BAD_REQUEST);
        exchange.setProperty(EXCEPTION_IS_SET, true);
        throw new ValidationException(errorMessage);
    }

    ;


    public static void buildException(Exchange exchange, String errorMessage, String errorMessageType) throws Exception {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? UNEXPECTED_ERROR_OCCURRED
                : errorMessageType);
        exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        exchange.setProperty(EXCEPTION_IS_SET, true);
        throw new Exception(errorMessage);
    }

    ;

    public static void setValidationExceptionOnExchange(Exchange exchange, String errorMessage, String errorMessageType) {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? VALIDATION_ERROR
                : errorMessageType);
        exchange.setHttpStatus(HTTP_BAD_REQUEST);
        exchange.setProperty(EXCEPTION_IS_SET, true);
    }

    ;


    public static void setInternalServerErrorOnExchange(Exchange exchange, String errorMessage, String errorMessageType) {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? UNEXPECTED_ERROR_OCCURRED
                : errorMessageType);
        exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        exchange.setProperty(EXCEPTION_IS_SET, true);
    }

    ;


    public static boolean isStringDouble(String str) {
        if (str.matches("-?\\d+(\\.?\\d+)?"))
            return true;
        else
            return false;

    }

    public static boolean isStringInteger(String str) {
        if (str.matches("\\d+"))
            return true;
        else
            return false;
    }

    public static boolean isStringBoolean(String str) {
        if (str.toLowerCase().equals("false") || str.toLowerCase().equals("true"))
            return true;
        else
            return false;
    }


    public static Number castToNumber(String value, TypesEnum type) {
        switch (type) {
            case INTEGER:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            default:
                return null;
        }
    }

}



