package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.HttpRetryException;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class ApplicationTests {
	
	@Value("${local.server.port}")
	private int port = 0;
	
	@Autowired
	private SecurityProperties security;

	@Test
	public void configurationAvailable() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = new TestRestTemplate("user", security.getUser().getPassword()).getForEntity("http://localhost:" + port + "/app/cloud", Map.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	@Test
	public void envPostAvailable() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = new TestRestTemplate("user", security.getUser().getPassword()).postForEntity("http://localhost:" + port + "/admin/env", form , Map.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	@Test
	public void envPostSecure() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> entity = new TestRestTemplate().postForEntity("http://localhost:" + port + "/admin/env", form, Map.class);
            fail("An HttpRetryException should have been raised here!");
        } catch (RuntimeException ex) {
            assertTrue(ex.getCause() instanceof HttpRetryException);
        }
	}

}
