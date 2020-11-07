package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPropertiesService {


    @Autowired
    private Environment environment;
}
