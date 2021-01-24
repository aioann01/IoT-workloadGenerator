package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Configs;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.AuthService;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.ApplicationPropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.*;

public class JwtRequestFilter implements Filter {

    @Autowired
    AuthService authService = new AuthService();

    protected final Log logger = LogFactory.getLog(getClass());

//    @Value("${Authorization.enabled}")
//    private Boolean authorizationEnabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpServletRequest request= (HttpServletRequest)servletRequest;
//        boolean enabledAuthorization = true;
//        try {
//            enabledAuthorization = Boolean.valueOf(Utils.readApplicationProperty(AUTHORIZATION_ENABLED_PROPERTY));
//        }catch (Exception exception){
//            String errorMessage = exception.getMessage();
//            logger.error(EXCEPTION_CAUGHT+errorMessage,exception);
//            ResponseEntityErrorHandler.changeFilterResponse(response, HTTP_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, errorMessage );
//            return;  }
        if(Boolean.valueOf(ApplicationPropertiesUtil.getEnabledAuthorization())) {

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
        logger.info("No Authorization needed.");
        filterChain.doFilter(request, response);
    }


    @Override
    public void destroy() {

    }
}
