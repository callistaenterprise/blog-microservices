package se.callista.microservises.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigServerApplication.class)
// Instruct embedded Tomcat to run on a random free port and skip talking to the Bus and the Discovery server
@IntegrationTest({"server.port=0", "spring.cloud.bus.enabled=false", "spring.cloud.discovery.enabled=false"})
public class ConfigServerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
