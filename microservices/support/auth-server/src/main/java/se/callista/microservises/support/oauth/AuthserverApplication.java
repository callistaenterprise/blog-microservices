package se.callista.microservises.support.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import java.security.Principal;

@RestController
@EnableResourceServer
@EnableAuthorizationServer
@SpringBootApplication
public class AuthserverApplication {

	private static final Logger LOG = LoggerFactory.getLogger(AuthserverApplication.class);

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	static {
		// for localhost testing only
		LOG.warn("Will now disable hostname check in SSL, only to be used during development");
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
	}

	public static void main(String[] args) {
		LOG.info("### STARTING ###");
		SpringApplication.run(AuthserverApplication.class, args);
		LOG.info("### STARTED ###");
	}
}
