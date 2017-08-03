package se.callista.microservises.workshop.zipkin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Instruct embedded Tomcat to run on a random free port and skip talking to the Config, Bus and Discovery server
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ZipkinServerApplication.class, webEnvironment=RANDOM_PORT)

public class ZipkinServerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
