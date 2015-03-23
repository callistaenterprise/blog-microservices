package zuulserver;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

// TODO: Temp fix
//import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;

/**
 * @author Spencer Gibb
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Controller
@EnableZuulProxy
// TODO: Temp fix
//@EnableOAuth2Sso

public class ZuulServerApplication {
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
    }
}
