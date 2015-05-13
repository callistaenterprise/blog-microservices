package se.callista.microservices.util;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by magnus on 01/05/15.
 */
@Configuration
public class LogConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(LogConfiguration.class);

    @Value("${spring.application.name}")
    String componentName;

    @Value("${app.ConnectTimeout:-1}")
    String connectTimeoutStr;

    @Value("${app.ReadTimeout:-1}")
    String readTimeoutStr;

    @Value("${app.http.header.corrId:X-corrId}")
    String http_header_corrId;

    @Value("${app.mdc.key.corrId:corrId}")
    String mdc_key_corrId;

    @Value("${app.mdc.key.component:component}")
    String mdc_key_component;

    @Value("${app.mdc.key.user:user}")
    String mdc_key_user;

    @Bean
    public Filter hystrixFilter() {
        LOG.debug("Declare my hystrixFilter");
        return hystrixFilter;
    }

    @Bean
    public Filter logFilter() {
        LOG.debug("Declare my logFilter");
        return logFilter;
    }

    @Bean
    public RestTemplate restTemplateWithLogInterceptor() {
        LOG.debug("Declare my restTemplate with a logInterceptor");
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.getInterceptors().add(logInterceptor);
        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {

        int connectTimeout = toInt(connectTimeoutStr, -1);
        int readTimeout = toInt(readTimeoutStr, -1);

        // If we need to do some more advanced stuff we beter use Appache HttpClient
        // HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        if (connectTimeout > 0) {
            LOG.debug("Set connectTimeout = {}", connectTimeout);
            factory.setConnectTimeout(connectTimeout);
        }
        if (readTimeout > 0) {
            LOG.debug("Set readTimeout = {}", readTimeout);
            factory.setReadTimeout(readTimeout);
        }

        return factory;
    }

    private int toInt(String str, int defVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defVal;
        }
    }

    private ClientHttpRequestInterceptor logInterceptor = (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {

        String corrId = MDC.get(mdc_key_corrId);
        LOG.debug("Add {} {} to HTTP header {}", mdc_key_corrId, corrId, http_header_corrId);
        HttpHeaders headers = request.getHeaders();
        headers.add(http_header_corrId, corrId);
        return execution.execute(request, body);
    };

    private LambdaServletFilter hystrixFilter = (ServletRequest req, ServletResponse resp, FilterChain chain) -> {

        LOG.debug("Init Hystrix Context...");
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();

        try {
            chain.doFilter(req, resp);

        } finally {
            LOG.debug("Shutting down Hystrix Context...");
            ctx.shutdown();
        }
    };

    private LambdaServletFilter logFilter = (ServletRequest req, ServletResponse resp, FilterChain chain) -> {

        HttpServletRequest httpReq = (HttpServletRequest) req;
        String corrId = httpReq.getHeader(http_header_corrId);
        LOG.debug("{} = [{}]", http_header_corrId, corrId);

        if (corrId == null || corrId.length() == 0) {
            corrId = UUID.randomUUID().toString();
            LOG.debug("Initiate corrId to {}", corrId);
        }

        LOG.debug("Storing in MDC: {} = {} and {} = {}", mdc_key_corrId, corrId, mdc_key_component, componentName);
        MDC.put(mdc_key_corrId, corrId);
        MDC.put(mdc_key_component, componentName);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String name = authentication.getName();
            LOG.debug("Storing in MDC: {} = {}", mdc_key_user, name);
            MDC.put(mdc_key_user, name);
        }

        try {
            chain.doFilter(req, resp);

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Remove from MDC: {} = {} and {} = {}",
                    mdc_key_corrId, MDC.get(mdc_key_corrId),
                    mdc_key_component, MDC.get(mdc_key_component));
            }
            MDC.remove(mdc_key_corrId);
            MDC.remove(mdc_key_component);

            if (authentication != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Remove from MDC: {} = {}", mdc_key_user, MDC.get(mdc_key_user));
                }
                MDC.remove(mdc_key_user);
            }
        }
    };
}
