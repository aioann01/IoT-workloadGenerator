package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Properties;

import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.EXCEPTION_CAUGHT;
import static cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils.FrameworkConstants.WORKLOAD_GENERATOR_PROPERTIES_WINDOWS;
@Configuration
//@PropertySource("classpath:application.properties")
@Service
public class ApplicationPropertiesUtil{

    private static final Logger log = LoggerFactory.getLogger(ApplicationPropertiesUtil.class);

    private static Properties workloadGeneratorProperties;

    private static String configsDirectory;

    private static String resourcesDirectory;

    private static String ssoServicePort;

    private static String ssoServiceIp;

    private static Boolean enabledAuthorization;


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void setWorkloadGeneratorProperties() {
        try {
            workloadGeneratorProperties = new Properties();
            workloadGeneratorProperties.load(new FileInputStream(configsDirectory + WORKLOAD_GENERATOR_PROPERTIES_WINDOWS));

        } catch (Exception exception) {
            log.error("Could not load workloadGenerator.properties from provided configs directory", exception);
        }
    }

    @Value("${configs.directory}")
    public void setConfigsDirectory(String configsDirectory) {
        ApplicationPropertiesUtil.configsDirectory = configsDirectory;
    }

    @Value("${resources.directory}")
    public void setResourcesDirectory(String resourcesDirectory) {
        ApplicationPropertiesUtil.resourcesDirectory = resourcesDirectory;
    }

    public static String getSsoServicePort() {
        return ssoServicePort;
    }

    @Value("${sso.service.port}")
    public  void setSsoServicePort(String ssoServicePort) {
        ApplicationPropertiesUtil.ssoServicePort = ssoServicePort;
    }

    public static String getSsoServiceIp() {
        return ssoServiceIp;
    }

    @Value("${sso.service.ip}")
    public  void setSsoServiceIp(String ssoServiceIp) {
        ApplicationPropertiesUtil.ssoServiceIp = ssoServiceIp;
    }

    public static Boolean getEnabledAuthorization() {
        return enabledAuthorization;
    }
    @Value("${Authorization.enabled}")
    public  void setEnabledAuthorization(Boolean enabledAuthorization) {
        ApplicationPropertiesUtil.enabledAuthorization = enabledAuthorization;
    }

    public static String getConfigsDirectory() {
        return configsDirectory;
    }

    public static String getResourcesDirectory() {
        return resourcesDirectory;
    }


    //WorkloadGenerator.properties
    public static String readPropertyFromConfigs(String propertyName) throws Exception {
        String errorMessage = null;
//        if (configsDirectory == null)
//            configsDirectory = readApplicationProperty(CONFIGS_DIRECTORY_PROPERTY);
        if (workloadGeneratorProperties == null) {
            try {
                workloadGeneratorProperties = new Properties();
                //Container
                workloadGeneratorProperties.load(new FileInputStream(configsDirectory + WORKLOAD_GENERATOR_PROPERTIES_WINDOWS));

                //local
                // properties.load(new FileInputStream("/home/configs/"+WORKLOAD_GENERATOR_PROPERTIES_WINDOWS));

            } catch (Exception e) {
                errorMessage = WORKLOAD_GENERATOR_PROPERTIES_WINDOWS + "file does not exist in " + configsDirectory;
                log.error(errorMessage);
                log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
                throw new Exception(errorMessage);
            }
        }
        try {
            return workloadGeneratorProperties.getProperty(propertyName);
        } catch (Exception e) {
            errorMessage = "Property {" + propertyName + "} could not be read from application.json";
            log.error(errorMessage);
            log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
            throw new Exception(errorMessage);
        }

    }


}
