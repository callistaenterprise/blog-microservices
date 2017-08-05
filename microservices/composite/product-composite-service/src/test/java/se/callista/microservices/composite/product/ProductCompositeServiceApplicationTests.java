package se.callista.microservices.composite.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Instruct embedded Tomcat to run on a random free port and skip talking to the Config, Bus and Discovery server
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ProductCompositeServiceApplication.class, webEnvironment=RANDOM_PORT, properties = {
	"spring.cloud.config.enabled=false",
	"spring.cloud.bus.enabled=false",
	"spring.cloud.discovery.enabled=false",
	"MY_CONFIG_USER=u",
	"MY_CONFIG_PWD=p"
})
public class ProductCompositeServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
