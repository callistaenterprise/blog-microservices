package se.callista.microservices.api.product;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY;
import static org.junit.Assert.assertEquals;

@Ignore
public class ProductApiServiceSystemIntegrationRestTemplateTests {

	private static RestTemplate client = null;
	HttpClient c2;

	@BeforeClass
	public static void configureRestTemplate() {
		client = new RestTemplate();

		client.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				return false;
			}
		});

	}

	@Test
	public void noAuthWrong() throws URISyntaxException {
		ResponseEntity<String> response = client.getForEntity(new URI("http://localhost:8080/login"), String.class);
		assertEquals(HttpStatus.FOUND, response.getStatusCode());
	}
}
