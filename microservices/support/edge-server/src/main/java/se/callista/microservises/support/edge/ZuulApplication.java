package se.callista.microservises.support.edge;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.stereotype.Controller;

// TODO: Temp fix
//import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;

@SpringBootApplication
@Controller
@EnableZuulProxy
// TODO: Temp fix
//@EnableOAuth2Sso
public class ZuulApplication {
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulApplication.class).web(true).run(args);
    }
}
