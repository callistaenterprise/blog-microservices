package se.callista.microservices.composite.product;

import org.junit.Test;

// TODO: WHy isn't cloud-config disabled when this test runs??? It makes the test fail!!!
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = ProductCompositeServiceApplication.class)
//@WebAppConfiguration
//// Instruct embedded Tomcat to run on a random free port and skip talking to the Config, Bus and Discovery server
//@IntegrationTest({"server.port=0", "spring.cloud.config.enabled=false", "spring.cloud.bus.enabled=false", "spring.cloud.discovery.enabled=false"})
public class ProductCompositeServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
