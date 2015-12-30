package se.callista.microservises.core.recommendation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RecommendationServiceApplication.class)
@WebAppConfiguration
// Instruct embedded Tomcat to run on a random free port and skip talking to the Config, Bus and Discovery server
@IntegrationTest({"server.port=0", "spring.cloud.config.enabled=false", "spring.cloud.bus.enabled=false", "spring.cloud.discovery.enabled=false"})
public class RecommendationServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
