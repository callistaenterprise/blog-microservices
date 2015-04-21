package se.callista.microservises.support.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
/*
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 */

/**
 * Created by magnus on 18/08/14.
 */
@Configuration
//@EnableWebSecurity
public class SecurityConfiguration { // extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

/*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        LOG.info("### Setting up auth, vI...");
        auth
            .inMemoryAuthentication()
            .withUser("user").password("password").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("### Setting up access rule, vII...");

        http
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
*/
}