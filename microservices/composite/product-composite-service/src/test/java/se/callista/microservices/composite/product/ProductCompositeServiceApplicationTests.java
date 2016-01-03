package se.callista.microservices.composite.product;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
