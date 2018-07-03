package se.callista.microservices.core.review;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Instruct embedded Tomcat to run on a random free port and skip talking to the Config, Bus and Discovery server
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ReviewServiceApplication.class, webEnvironment=RANDOM_PORT, properties = {
	"spring.cloud.config.enabled=false",
	"spring.cloud.bus.enabled=false",
	"spring.cloud.discovery.enabled=false",
	"my-secret-property=my-secret-test-property",
	"MY_CONFIG_USER=u",
	"MY_CONFIG_PWD=p",
	"spring.datasource.platform=h2",
	"spring.datasource.url=jdbc:h2:mem:idpDbTest"
})

public class ReviewServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
