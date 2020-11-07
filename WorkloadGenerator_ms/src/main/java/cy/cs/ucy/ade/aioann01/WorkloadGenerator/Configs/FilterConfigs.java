package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Configs;

import io.jsonwebtoken.Jwt;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfigs {


    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtRequestFilterFilterRegistrationBean() {
        FilterRegistrationBean<JwtRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtRequestFilter());
        registrationBean.addUrlPatterns("/workloadGenerator/*");
        return registrationBean;


    }


}


