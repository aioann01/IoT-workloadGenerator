package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.SendServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Enums.SensorMessageEnum;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Http.SensorMessage;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model.Server;
import cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services.ISensorMessageSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.SIMPLE_DATE_FORMAT_FOR_SORTED_CSV;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.WorkloadGeneratorConstants.*;

@Service
public class HttpSensorMessageRequestService implements ISensorMessageSendService {

    @Autowired
    RestTemplate restTemplate = new RestTemplate();


    private List<Server> httpServers;

    private String requestURI;

    private static final Logger log = LoggerFactory.getLogger(HttpSensorMessageRequestService.class);


    @Override
    public void initializeServiceReceiverConfigurations(List<Server> httpServers, HashMap<String, String> configs) throws Exception {
        this.httpServers = httpServers;
        this.requestURI = configs.get(REQUEST_URI);
    }

    @Override
    public void sendMessage(String sensorId, String message, SensorMessageEnum contentType) throws Exception {
        for (Server httpServer : httpServers) {
            String serverIp = httpServer.getServerIp();

            String serverPort = httpServer.getServerPort();

            try {
                StringBuilder url = new StringBuilder();
                url.append("http://").append(serverIp).append(":").append(serverPort).append(requestURI);
                HttpHeaders requestHeaders = new HttpHeaders();
                String payload;
                switch (contentType) {
                    case XML:
                        requestHeaders.setContentType(MediaType.APPLICATION_XML);
                        StringBuilder xmlMessage = new StringBuilder();
                        xmlMessage.append(SENSOR_ID_XML_ELEMENT_START + sensorId + SENSOR_ID_XML_ELEMENT_END);
                        xmlMessage.append(MESSAGE_XML_ELEMENT_START + message + MESSAGE_XML_ELEMENT_END);
                        payload = ROOT_XML_ELEMENT_START + xmlMessage.toString() + ROOT_XML_ELEMENT_END;
                        break;
                    case JSON:
                        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                        payload = new ObjectMapper().writeValueAsString(new SensorMessage(message, sensorId));
                        break;
                    case TEXT:
                    default:
                        requestHeaders.setContentType(MediaType.TEXT_PLAIN);
                        payload = new SensorMessage(message, sensorId).toString();
                        break;
                }
                requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_FOR_SORTED_CSV);
                simpleDateFormat.format(Calendar.getInstance().getTime());
                requestHeaders.setDate(Calendar.getInstance().getTimeInMillis());
                //  log.debug("Sending request to :" + url.toString());
                //    log.trace("Sending request to: "+url.toString()+" Payload: " +message+" for sensorId {"+sensorId+"} "+ "Time Sent:"+ new Date(requestHeaders.getDate()));

                HttpEntity<String> entity = new HttpEntity<>(payload, requestHeaders);
                ResponseEntity response = restTemplate.postForEntity(url.toString(), entity, String.class);
            } catch (Exception e) {
                log.error("Exception caught while sending request to HTTP server: " + serverIp + ":" + serverPort + " for sensor {" + sensorId + "+} :" + e.getMessage(), e);
                throw new Exception("Could not send message to HTTP server: " + serverIp + ":" + serverPort + " due to:" + e.getMessage());
            }
        }
    }

    @Override
    public void terminate() throws Exception {

    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
