package cy.cs.ucy.ade.aioann01.WorkloadGenerator.HttpServer.HttpServer;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.net.URISyntaxException;

@SpringBootApplication
public class TestingClient  {

    private static final Logger log= LoggerFactory.getLogger(TestingClient.class);



    @EventListener(ApplicationReadyEvent.class)
    public static void main(String [] args) throws MqttException, URISyntaxException {
        Subscriber subscriber = new Subscriber("https://mqtt-broker:1883");
    }
}
