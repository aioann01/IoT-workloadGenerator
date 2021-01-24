package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;


import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.TypesEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ValidationException;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.Interface.ISensorDataProducerService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SensorProtototypeServices.ProduceMockSensorDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;
import java.util.Optional;


import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;
@Component
public class CommonUtils<T> {

    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);


    public static Optional<ProduceMockSensorDataService> findProducerMockSensorDataServiceFromExistingISensorDataProducerServices(List<ISensorDataProducerService> sensorDataProducerServices){
        return sensorDataProducerServices.stream()
                .filter(iSensorDataProducerService -> iSensorDataProducerService instanceof ProduceMockSensorDataService)
                .map(iSensorDataProducerService -> (ProduceMockSensorDataService)iSensorDataProducerService)
                .findAny();
    }


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



    public static void buildException(Exchange exchange, String errorMessage, String errorMessageType) throws Exception {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? UNEXPECTED_ERROR_OCCURRED
                : errorMessageType);
        exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        exchange.setProperty(EXCEPTION_IS_SET, true);
        throw new Exception(errorMessage);
    }


    public static void setValidationErrorOnExchange(Exchange exchange, String errorMessage, String errorMessageType) {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? VALIDATION_ERROR
                : errorMessageType);
        exchange.setHttpStatus(HTTP_BAD_REQUEST);
        exchange.setProperty(EXCEPTION_IS_SET, true);
    }

    public static void setNotFoundErrorOnExchange(Exchange exchange, String errorMessage, String errorMessageType) {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? VALIDATION_ERROR
                : errorMessageType);
        exchange.setHttpStatus(HTTP_NOT_FOUND);
        exchange.setProperty(EXCEPTION_IS_SET, true);
    }


    public static void setInternalServerErrorOnExchange(Exchange exchange, String errorMessage, String errorMessageType) {
        exchange.setProperty(ERROR_MESSAGE, errorMessage);
        exchange.setProperty(ERROR_MESSAGE_TYPE, errorMessageType == null
                ? UNEXPECTED_ERROR_OCCURRED
                : errorMessageType);
        exchange.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        exchange.setProperty(EXCEPTION_IS_SET, true);
    }


    public static void printHostedMachineStats(){
        int availableMachineCores = Runtime.getRuntime().availableProcessors();
        long machineMemory = Runtime.getRuntime().maxMemory();
        log.debug("*******Machine stats******\n" + "Available Machine cores = "+ availableMachineCores+"\n" + "Machine Max memory = "+machineMemory+"\n");
    }

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



