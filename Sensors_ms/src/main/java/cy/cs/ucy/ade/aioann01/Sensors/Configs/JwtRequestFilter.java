package cy.cs.ucy.ade.aioann01.Sensors.Configs;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.Sensors.Services.AuthService;
import cy.cs.ucy.ade.aioann01.Sensors.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.*;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    AuthService authService=new AuthService();

    @Value("${Authorization.enabled}")
    private boolean enabledAuthorization;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws ServletException, IOException  {
        boolean enabledAuthorization=true;
        try {
            enabledAuthorization= Boolean.valueOf(Utils.readPropertyFromConfigs(AUTHORIZATION_ENABLED_PROPERTY));
        }catch (Exception exception){
            String errorMessage = exception.getMessage();
            logger.error(EXCEPTION_CAUGHT+errorMessage,exception);
            ResponseEntityErrorHandler.changeFilterResponse(response, HTTP_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, errorMessage );
            return;  }
        if(enabledAuthorization){
            final String authorizationTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
            logger.debug("Entered JwtRequestFilter");

            if (authorizationTokenHeader == null) {
                String errorMessage = "Authorization header in request is not provided";
                logger.error(errorMessage);
                ResponseEntityErrorHandler.changeFilterResponse(response, HTTP_UNAUTHORIZED, AUTHORIZATION_ERROR, errorMessage);
                return;
            }
            if (authorizationTokenHeader != null) {
                try {
                    logger.info("Preparing request to SSO for authorization");
                    Exchange ssoResponse = authService.authorizeToSSO(request.getRequestURL().toString(), authorizationTokenHeader);
                    if (ssoResponse.getHttpStatus() == HTTP_SUCCESS) {

                        if ((Boolean) ssoResponse.getProperty("Authorized") == true)
                            logger.info("Successfully Authorized");
                        else {
                            String errorMessage = ssoResponse.getProperty(ERROR_MESSAGE).toString();
                            ResponseEntityErrorHandler.changeFilterResponse(response, HTTP_UNAUTHORIZED, null, errorMessage == null ? "Not error code provided.Please check SSO logs" : errorMessage);
                            logger.error("Retrieved Unauthorized response from SSO");
                            return;
                        }
                    } else if (ssoResponse.getHttpStatus() == HTTP_INTERNAL_SERVER_ERROR) {
                        String errorMessage = ssoResponse.getProperty(ERROR_MESSAGE).toString();
                        logger.error("Internal Server error happened in SSO");
                        ResponseEntityErrorHandler.changeFilterResponse(response, HTTP_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, errorMessage == null ? "Not error code provided.Please check SSO logs" : errorMessage);
                        return;
                    }
                } catch (Exception exception) {
                    ResponseEntityErrorHandler.changeFilterResponse(response, HTTP_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, exception.getMessage());
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

}
