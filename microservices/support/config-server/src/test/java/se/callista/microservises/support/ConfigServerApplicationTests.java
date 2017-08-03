package se.callista.microservises.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Instruct embedded Tomcat to run on a random free port and skip talking to the Config, Bus and Discovery server
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ConfigServerApplication.class, webEnvironment=RANDOM_PORT, properties = {
		"spring.cloud.bus.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"MY_CONFIG_ENCRYPT_KEY=my-very-secret-encryption-key"
})
public class ConfigServerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
