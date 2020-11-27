package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Configs;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Configs.ResponseEntityErrorHandler;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.ResponseMessage;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;

@ControllerAdvice
public class ExchangeResponseBodyAdvice<T>   implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        System.out.println("In supports() method of " + getClass().getSimpleName());
        return /*returnType.getContainingClass() == SensorController.class &&*/ returnType.getParameterType() == ResponseEntity.class||returnType.getParameterType() == Exchange.class;
    }


    @Override
    public Object beforeBodyWrite(Object controllerResponse, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        System.out.println("In beforeBodyWrite() method of " + getClass().getSimpleName());
        if(returnType.getParameterType() == Exchange.class){
            Exchange exchange = (Exchange)controllerResponse;
            HttpStatus httpStatus = exchange.getHttpStatus();
            response.setStatusCode(httpStatus);
            if(!(httpStatus == HTTP_SUCCESS || httpStatus == HTTP_CREATED || httpStatus == HTTP_NO_CONTENT)){
                String errorMessageType=(String)exchange.getProperty(ERROR_MESSAGE_TYPE,String.class);
                String exchangeErrorMessage=(String)exchange.getProperty(ERROR_MESSAGE);
                String errorMessage=errorMessageType == null ? exchangeErrorMessage:errorMessageType+":"+exchangeErrorMessage;
                ResponseMessage responseMessage = new ResponseMessage(errorMessage);
                exchange.setBody(responseMessage);}
            return  exchange.getBody();}
        else if (returnType.getParameterType() == ResponseEntity.class){
            if(controllerResponse instanceof HashMap)
            {
                HashMap<String, Object> responseFields = (HashMap<String, Object>) controllerResponse;
                ResponseMessage responseMessage = new ResponseMessage("");
                if (responseFields.containsKey("status")) {
                    int status = (Integer) responseFields.get("status");
                    response.setStatusCode(HttpStatus.valueOf(status));
                }
                if (responseFields.containsKey("error"))
                    responseMessage.setMessage(responseFields.get("error").toString());

                return responseMessage;
            }
            else
                return controllerResponse;
        }
        else{return controllerResponse;}
    }
}
