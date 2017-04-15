package se.callista.microservises.workshop.zipkin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/*

THIS IS FOR SPRING BOOT 1.4 and SPRING CLOUD CAMDEN
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Instruct embedded Tomcat to run on a random free port and skip talking to the Config Server and the Bus
// Regarding settings of java.security.egd, see http://wiki.apache.org/tomcat/HowTo/FasterStartUp#Entropy_Source
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ZipkinServerApplication.class, webEnvironment=RANDOM_PORT, properties = {
	"spring.cloud.bus.enabled=false",
	"spring.cloud.config.enabled=false",
	"spring.sleuth.stream.enabled=false",
    "java.security.egd=file:/dev/./urandom",
	"logging.level.org.springframework.amqp.rabbit.core.RabbitAdmin=ERROR"
})
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ZipkinServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class ZipkinServerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
