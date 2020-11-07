package cy.cs.ucy.ade.aioann01.Sensors.Services;


import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.Sensors.Model.Http.ResponseMessage;
import cy.cs.ucy.ade.aioann01.Sensors.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static cy.cs.ucy.ade.aioann01.Sensors.Utils.FrameworkConstants.*;

@Service
public class AuthService {

    @Autowired
    RestTemplate restTemplate=new RestTemplate();

    private static final Logger log= LoggerFactory.getLogger(AuthService.class);

    public Exchange authorizeToSSO(String requestUri, String authorizationHeader) throws Exception{

        try {
            String sso_port = Utils.readPropertyFromConfigs(SSO_PORT_PROPERTY_NAME);
            String sso_host = Utils.readPropertyFromConfigs(SSO_SERVICE_HOST_PROPERTY_NAME);
            StringBuilder url = new StringBuilder();
            url.append("http://").append(sso_host).append(":").append(sso_port).append(AUTHORIZE_URI);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add(AUTHORIZATION_HEADER, authorizationHeader);
            requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            requestHeaders.add(REQUEST_URI_TO_BE_CHECK_FOR_AUTHORIZATION_HEADER, requestUri);
            HttpEntity requestEntity = new HttpEntity<>(requestHeaders);
            log.info("Sending request to :" + url.toString());
            ResponseEntity ssoResponse= restTemplate.exchange(url.toString(), HttpMethod.POST, requestEntity, Exchange.class);
            Exchange ssoResponseExchange=(Exchange)ssoResponse.getBody();
            return  ssoResponseExchange;

        }catch (Exception e){
            log.error(EXCEPTION_CAUGHT+e.getMessage(),e);
            throw new Exception(e.getCause().getMessage() +"  to SSO");}
    }


    @Bean(name = "AuthServiceBean")
    @Scope("prototype")
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
