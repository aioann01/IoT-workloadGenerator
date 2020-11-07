package cy.cs.ucy.ade.aioann01.Sensors.Configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.*;

@ControllerAdvice
public class ResponseEntityErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger log= LoggerFactory.getLogger(ResponseEntityErrorHandler.class);


    @ExceptionHandler(value= {UsernameNotFoundException.class, IllegalArgumentException.class, IllegalStateException.class,Exception.class  })
    protected  ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        String exceptionClassName=ex.getClass().getSimpleName();
        log.error("Exception Caught: "+ex.getMessage(),ex);
        switch(exceptionClassName){
            case "BadCredentialsException": return  buildErrorResponse(AUTHENTICATION_ERROR,ex.getMessage(),HTTP_FORBIDDEN);
            case "UsernameNotFoundException": return  buildErrorResponse(AUTHENTICATION_ERROR,ex.getMessage(),HTTP_FORBIDDEN);
            default: return  buildErrorResponse(UNEXPECTED_ERROR_OCCURRED,ex.getMessage(),HTTP_INTERNAL_SERVER_ERROR);
        }
    }


    public static void changeFilterResponse(HttpServletResponse response,HttpStatus httpStatus,String errorMessageType,String errorMessage){
        ResponseMessage responseBody=null;
        if(errorMessageType!=null)
            responseBody= new ResponseMessage(errorMessageType+":"+errorMessage);
        else
            responseBody= new ResponseMessage(errorMessage);

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);
            response.getWriter().write(jsonResponse);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ResponseEntity<Object> buildErrorResponse(String errorMessageType,String errorMessage,HttpStatus httpStatus){
        ResponseMessage responseBody=null;
        if(errorMessageType!=null)
            responseBody= new ResponseMessage(errorMessageType+":"+errorMessage);
        else
            responseBody= new ResponseMessage(errorMessage);
        return new ResponseEntity(responseBody, httpStatus);
    }


}