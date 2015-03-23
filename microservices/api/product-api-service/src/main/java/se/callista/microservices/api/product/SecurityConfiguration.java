package se.callista.microservices.api.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Created by magnus on 18/08/14.
 */
@Configuration
//@EnableWebMvcSecurity
public class SecurityConfiguration { // TODO: sec-config disabled for now, so hystrix will get auth-problems... extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

    /*
    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.
            jdbcAuthentication()
            .dataSource(dataSource)
            .withDefaultSchema();
    }
    */

//    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("### Setting up access to hystrix, vIII...");

        // TODO: Can't get this to work so for now security is in fact disabled, e.g. ".anyRequest().authenticated()" is a comment
        http
            .authorizeRequests()
                .antMatchers("/hystrix.stream").permitAll()
//                .anyRequest().authenticated()
        ;

        LOG.info("### Setting up access to hystrix, done!");
    }

}