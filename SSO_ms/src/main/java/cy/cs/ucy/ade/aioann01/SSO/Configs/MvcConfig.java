package cy.cs.ucy.ade.aioann01.SSO.Configs;



import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan("com.baeldung.web.controller")
public class MvcConfig implements  WebMvcConfigurer{




        public MvcConfig() {
            super();
        }

        // API

//        @Override
//        public void addViewControllers(final ViewControllerRegistry registry) {
//            registry.addViewController("/anonymous.html");
//
//            registry.addViewController("/login.html");
//            registry.addViewController("/homepage.html");
//            registry.addViewController("/console.html");
//            registry.addViewController("/csrfHome.html");
//        }
//
//        @Bean
//        public ViewResolver viewResolver() {
//            final InternalResourceViewResolver bean = new InternalResourceViewResolver();
//
//            bean.setViewClass(JstlView.class);
//            bean.setPrefix("/WEB-INF/view/");
//            bean.setSuffix(".jsp");
//
//            return bean;
//        }

        @Override
        public void addInterceptors(final InterceptorRegistry registry) {
            registry.addInterceptor(new ExchangeInterceptor());
//            registry.addInterceptor(new UserInterceptor());
//            registry.addInterceptor(new SessionTimerInterceptor());

    }}
