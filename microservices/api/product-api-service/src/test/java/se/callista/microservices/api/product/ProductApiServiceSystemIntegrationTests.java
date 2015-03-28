package se.callista.microservices.api.product;

import static org.junit.Assert.*;
import static org.apache.http.HttpStatus.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Ignore
public class ProductApiServiceSystemIntegrationTests {

	private static DefaultHttpClient client = new DefaultHttpClient();
	private static HttpParams        params = new BasicHttpParams();

	private HttpResponse response = null;

	@BeforeClass
	public static void configureRestTemplate() {
		client.getCredentialsProvider().setCredentials(
			new AuthScope("localhost", 9999),
			new UsernamePasswordCredentials("user", "password"));

		params.setParameter(ClientPNames.HANDLE_REDIRECTS, false); // actual raw parameter
		// HttpClientParams.setRedirecting(params, false); // alternative
	}

	@After
	public  void cleanup() {
		if (response != null) EntityUtils.consumeQuietly(response.getEntity());
	}


	@Test
	public void notAuthenticated() throws IOException, AuthenticationException {
		// Try to acces a page without an initial login...
		performHttpGetAndAssertStatusCodeWithAutoRedirect("http://localhost:8080/login", SC_UNAUTHORIZED);
		performHttpGetAndAssertStatusCodeWithAutoRedirect("http://localhost:8080/productapi/products/123", SC_UNAUTHORIZED);
	}


	@Test
	public void authOkUsingOAuth() throws IOException, AuthenticationException {
	}


	@Test
	public void authOkUsingWebApp() throws IOException, AuthenticationException {

		// -------------------------------
		// 1. Try to access a resource...
		// -------------------------------
		String resourceUrl = "http://localhost:8080/productapi/products/123";
		response = performHttpGetAndAssertStatusCode(resourceUrl, SC_MOVED_TEMPORARILY);

		// Pick up and assert the login redirect
		String redirectLoginUrl = response.getHeaders("Location")[0].getValue();

		String expectedLoginUrl = "http://localhost:8080/login";
		assertEquals(expectedLoginUrl, redirectLoginUrl);
		EntityUtils.consumeQuietly(response.getEntity());


		// ------------------
		// 2. Try to login...
		// ------------------
		response = performHttpGetAndAssertStatusCode(redirectLoginUrl, SC_MOVED_TEMPORARILY);

		// Pick up and assert the OAuth redirect
		String redirectAuthorizeUrl = response.getHeaders("Location")[0].getValue();
		String expectedLocationStart = "http://localhost:9999/uaa/oauth/authorize?client_id=acme&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin&response_type=code&state=";
		assertTrue(redirectAuthorizeUrl.startsWith(expectedLocationStart));
		EntityUtils.consumeQuietly(response.getEntity());


		// ----------------------------------------------------
		// 3. Try to login to the OAuth server using basic auth
		// ----------------------------------------------------

		response = performHttpGet(redirectAuthorizeUrl);

		String redirectLoginWithCodeGrantUrl = null;
		int sc = response.getStatusLine().getStatusCode();
		assertTrue(sc == SC_OK || sc == SC_MOVED_TEMPORARILY);

		if (sc == SC_MOVED_TEMPORARILY) {

			// User consent is already given, just proceed with the login...
			redirectLoginWithCodeGrantUrl = response.getHeaders("Location")[0].getValue();

		} else if (sc == SC_OK) {

			// We need to ask the user for its consent...
			String approvalPage = getBodyAsString(response);
			String expectedApprovalPage = "<html><body><h1>OAuth Approval</h1><p>Do you authorize 'acme' to access your protected resources?</p><form id='confirmationForm' name='confirmationForm' action='/uaa/oauth/authorize' method='post'><input name='user_oauth_approval' value='true' type='hidden'/><ul><li><div class='form-group'>scope.openid: <input type='radio' name='scope.openid' value='true'>Approve</input> <input type='radio' name='scope.openid' value='false' checked>Deny</input></div></li></ul><label><input name='authorize' value='Authorize' type='submit'/></label></form></body></html>";
			assertEquals(expectedApprovalPage, approvalPage);

			// Extract the jsessionid to be able to feed it into the curl-workaround in the next step
			String jsession = response.getHeaders("Set-Cookie")[0].getValue();
			int pos = jsession.indexOf(';');
			jsession = jsession.substring(0, pos);

			EntityUtils.consumeQuietly(response.getEntity());

			// -----------------------------------------------------
			// 4. Post an user consent using the OAuth approval form
			// -----------------------------------------------------
			String command = "curl http://localhost:9999/uaa/oauth/authorize -i " +
					"-H \"Cookie: " + jsession + "\" " +
					"-H \"Authorization: Basic dXNlcjpwYXNzd29yZA==\" " +
					"--data \"user_oauth_approval=true&scope.openid=true&authorize=Authorize\"";
			String result = execute(command);

			int statusCode = getStatusCodeFromCurlResult(result);

			// Verify that we got a redirect
			assertEquals(SC_MOVED_TEMPORARILY, statusCode);

			redirectLoginWithCodeGrantUrl = getHeaderFromCurlResult(result, "Location");
		}

		System.err.println("Redirect URL for sending back the code grant: " + redirectLoginWithCodeGrantUrl);
		Map<String, String> paramMap = getQueryParameters(redirectLoginWithCodeGrantUrl);

		// Verify that the redirect from the request looks like: http://localhost:8080/login?code=jDPf6c&state=tLKtpi
		assertTrue("Expected start: " + expectedLoginUrl + ", found: " + redirectLoginWithCodeGrantUrl, redirectLoginWithCodeGrantUrl.startsWith(expectedLoginUrl));
		assertEquals(2, paramMap.size());
		assertNotNull(paramMap.get("code"));
		assertNotNull(paramMap.get("state"));

		EntityUtils.consumeQuietly(response.getEntity());

		//
		// 5. Redo the logging, but now with the code grant...
		//
		response = performHttpGetAndAssertStatusCode(redirectLoginWithCodeGrantUrl, SC_MOVED_TEMPORARILY);
		String redirectStartPageUrl = response.getHeaders("Location")[0].getValue();
		assertEquals(resourceUrl, redirectStartPageUrl);

		EntityUtils.consumeQuietly(response.getEntity());

		//
		// 6. Get user info
		//
		response = performHttpGetAndAssertStatusCode("http://localhost:8080/user", SC_OK);
		String userInfo = getBodyAsString(response);
		System.err.println("User: " + userInfo);
		assertTrue(userInfo.contains("\"username\":\"user\""));

		EntityUtils.consumeQuietly(response.getEntity());

		//
		// 7. Finally, get the OAuth protected resource
		//
		for (int i = 0; i < 1; i++) {
			System.err.println(i);
			EntityUtils.consumeQuietly(response.getEntity()); // LEave the last call open for the tests below...
			response = performHttpGetAndAssertStatusCode(resourceUrl, SC_OK);
		}
		String resourceInfo = getBodyAsString(response);
		System.err.println("Resource: " + resourceInfo);
		String expectedResourceInfo = "{\"productId\":123,\"name\":\"name\",\"weight\":123,\"recommendations\":[{\"recommendationId\":0,\"author\":\"Author 1\",\"rate\":1},{\"recommendationId\":0,\"author\":\"Author 2\",\"rate\":2},{\"recommendationId\":0,\"author\":\"Author 3\",\"rate\":3}],\"reviews\":[{\"reviewId\":1,\"author\":\"Author 1\",\"subject\":\"Subject 1\"},{\"reviewId\":2,\"author\":\"Author 2\",\"subject\":\"Subject 2\"},{\"reviewId\":3,\"author\":\"Author 3\",\"subject\":\"Subject 3\"}]}";
		assertEquals(expectedResourceInfo, resourceInfo);

		EntityUtils.consumeQuietly(response.getEntity());
	}

	private Map<String, String> getQueryParameters(String redirectLoginWithCodeGrant) {
		int pos2 = redirectLoginWithCodeGrant.indexOf('?');
		String params2 = redirectLoginWithCodeGrant.substring(pos2 + 1);
		List<NameValuePair> paramsList2 = URLEncodedUtils.parse(params2, Charset.defaultCharset());
		paramsList2.forEach(p -> System.err.println(p.getName() + " = " + p.getValue()));
		return paramsList2.stream().collect(Collectors.toMap(p -> p.getName(), p -> p.getValue()));
	}
		/*

POST /uaa/oauth/authorize HTTP/1.1
Host: localhost:9999
Connection: keep-alive
Content-Length: 62
Cache-Control: max-age=0
Authorization: Basic dXNlcjpwYXNzd29yZA==
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* /*;q=0.8
Origin: http://localhost:9999
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.104 Safari/537.36
Content-Type: application/x-www-form-urlencoded
Referer: http://localhost:9999/uaa/oauth/authorize?client_id=acme&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin&response_type=code&state=bBmmdQ
Accept-Encoding: gzip, deflate
Accept-Language: en-US,en;q=0.8,sv;q=0.6
Cookie: JSESSIONID=uNPCfjHyZxtYT-x00GIGFoS0; JSESSIONID=F99D0D48738941118A173F4A392E5D4F; XSRF-TOKEN=f6897d05-3078-4683-9a54-ca40ef931c37

curl 'http://localhost:9999/uaa/oauth/authorize'
		-H 'Cookie: JSESSIONID=uNPCfjHyZxtYT-x00GIGFoS0; JSESSIONID=F99D0D48738941118A173F4A392E5D4F; XSRF-TOKEN=f6897d05-3078-4683-9a54-ca40ef931c37'
		-H 'Origin: http://localhost:9999'
		-H 'Accept-Encoding: gzip, deflate'
		-H 'Accept-Language: en-US,en;q=0.8,sv;q=0.6'
		-H 'Authorization: Basic dXNlcjpwYXNzd29yZA=='
		-H 'Content-Type: application/x-www-form-urlencoded'
		-H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* /*;q=0.8'
		-H 'Cache-Control: max-age=0'
		-H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.104 Safari/537.36'
		-H 'Connection: keep-alive'
		-H 'Referer: http://localhost:9999/uaa/oauth/authorize?client_id=acme&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin&response_type=code&state=bBmmdQ'
		--data 'user_oauth_approval=true&scope.openid=true&authorize=Authorize'
		--compressed
		*/

/*
		HttpPost httpPost = new HttpPost("http://localhost:9999/uaa/oauth/authorize");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("user_oauth_approval", "true"));
		nvps.add(new BasicNameValuePair("scope.openid", "true"));
		nvps.add(new BasicNameValuePair("authorize", "Authorize"));

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("user", "password");
		httpPost.setHeader(new BasicScheme().authenticate(creds, httpPost));

		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* /*;q=0.8");
		httpPost.setHeader("Origin", "http://localhost:9999");
		httpPost.setHeader("Referer", location);
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.8,sv;q=0.6");

//		httpPost.setHeader("Cookie", jsession);

		System.err.println("\n### REQUEST HEADERS");
		for (Header h: httpPost.getAllHeaders()) {
			System.err.println("### " + h.getName() + " = " + h.getValue());
		}

		System.err.println("\n### CLIENT COOKIES #2.PRE:");
		printCookies();

		HttpResponse response = client.execute(httpPost);

		HttpEntity entity = response.getEntity();
		System.err.println("SC: " + response.getStatusLine());
		String code = getBodyAsString(response);
		System.err.println("CODE: " + code);

		System.err.println("\n### RESPONSE HEADERS #2:");
		for (Header h: response.getAllHeaders()) {
			System.err.println("### " + h.getName() + " = " + h.getValue());
		}

		System.err.println("\n### CLIENT COOKIES #2:");
		printCookies();

		EntityUtils.consumeQuietly(entity);
	}
*/

	private int getStatusCodeFromCurlResult(String result) {
		// Extract the status code from the first line that looks like:
		//   HTTP/1.1 302 Found
		final String SC_HEADER = "HTTP/1.1";
		int pos = result.indexOf(SC_HEADER);
		pos += SC_HEADER.length() + 1;
		String scStr = result.substring(pos, pos + 3);
		return Integer.parseInt(scStr);
	}

	private String getHeaderFromCurlResult(String result, String headerName) {
		// Extract the status code from the first line that looks like:
		// (givet that headerName = Location)
		//   Location: http://localhost:8080/login?code=7PJmGS&state=7IN9Fy
		int pos = result.indexOf(headerName);
		pos+= headerName.length() + 2;
		int posCR = result.indexOf('\n', pos);
		String headerValue = result.substring(pos, posCR);
		return headerValue;
	}

	private String execute(String command) {
		try {
			// Get runtime
			java.lang.Runtime rt = java.lang.Runtime.getRuntime();
			// Start a new process: Linux/UNIX command
			String[] cmd = { "/bin/sh", "-c", command };
			java.lang.Process p = rt.exec(cmd);
			// You can or maybe should wait for the process to complete
			p.waitFor();
			System.out.println("Process exited with code = " + p.exitValue());

			// Check for errors
			String error = getAsString(p.getErrorStream());
//			if (error != null) throw new RuntimeException(error);
			System.err.println("ERR: " + error);

			// Get process output
			String result = getAsString(p.getInputStream());
			return result;

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String getAsString(InputStream is) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String result = "";
			String s = null;
			while ((s = reader.readLine()) != null) {
				result += s + '\n';
			}
			return result;
		} finally {
			is.close();
		}
	}

	private void printCookies() {
		List<Cookie> cookies = client.getCookieStore().getCookies();
		for (Cookie c : cookies) {
			System.err.println(c.getName() + " = " + c.getValue() + ", " + c.getDomain() + ", " + c.getPorts() + ", " + c.getPath());
		}
	}

	private HttpResponse performHttpGet(String url) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setParams(params);
		HttpResponse response = client.execute(httpGet);
		return response;
	}

	private HttpResponse performHttpGetAndAssertStatusCode(String url, int expectedStatusCode) throws ClientProtocolException, IOException {
		HttpResponse response = performHttpGet(url);
		assertEquals(expectedStatusCode, response.getStatusLine().getStatusCode());
		return response;
	}

	/* private parts */

	private String getBodyAsString(HttpResponse r) throws IOException {
		return IOUtils.toString(r.getEntity().getContent());
	}

	/*
	 * NOT WORKING EXAMPLES, HC PERFORMS AUTOMATIC REDIRECT THAT BREAKS THE LOGIC...
	 */
	@Ignore
	@Test
	public void noAuthNotWorking() throws IOException {
		response = performHttpGetAndAssertStatusCodeWithAutoRedirect("http://localhost:8080/login", SC_UNAUTHORIZED);

		System.err.println("SC = " + response.getStatusLine());
		System.err.println("Body = " + getBodyAsString(response));
		org.apache.http.entity.ByteArrayEntity m;
		Header[] headers = response.getAllHeaders();
		for (Header h: headers) {
			System.err.println("### " + h.getName() + "=" + h.getValue());
		}
	}

	private HttpResponse performHttpGetAndAssertStatusCodeWithAutoRedirect(String url, int expectedStatusCode) throws ClientProtocolException, IOException {
		HttpResponse response = Request.Get(url).execute().returnResponse();
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals(expectedStatusCode, statusCode);

		return response;
	}

	private void performHttpPostAndAssertReturnCodeNotWorking(String url, int expectedHttpResultCode) throws ClientProtocolException, IOException {

		Request.Post("http://targethost/login")
				.bodyForm(Form.form().add("username",  "vip").add("password",  "secret").build())
				.execute().returnContent();

	}


}
