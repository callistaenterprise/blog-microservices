package se.callista.microservices.util;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
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

    @Value("${spring.application.name}")
    String componentName;

    @Bean
    public Filter hystrixFilter() {
        System.err.println("### v1. Declare my hystrixFilter");
        return hystrixFilter;
    }

    @Bean
    public Filter logFilter() {
        System.err.println("### v1. Declare my logFilter");
        return logFilter;
    }

    @Bean
    public RestTemplate restTemplateWithLogInterceptor() {
        System.err.println("### v1. Declare my restTemplate with a logInterceptor");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(logInterceptor);
        return restTemplate;
    }

    private ClientHttpRequestInterceptor logInterceptor = (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
        HttpHeaders headers = request.getHeaders();
        System.err.println("### " + tn() + " Add corrId " + MDC.get("corrId") + " to HTTP header X-corrId");
        headers.add("X-corrId", (String) MDC.get("corrId"));
        return execution.execute(request, body);
    };

    private LambdaServletFilter hystrixFilter = (ServletRequest req, ServletResponse resp, FilterChain chain) -> {

        System.err.println("### " + tn() + " Init Hystrix Context...");
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        try {
            chain.doFilter(req, resp);
        } finally {
            System.err.println("### " + tn() + " Shutting down Hystrix Context...");
            ctx.shutdown();
        }
    };

    private LambdaServletFilter logFilter = (ServletRequest req, ServletResponse resp, FilterChain chain) -> {

        HttpServletRequest httpReq = (HttpServletRequest) req;
        String corrId = httpReq.getHeader("X-corrId");
        System.err.println("### " + tn() + " X-corrId = [" + corrId + "]");

        if (corrId == null || corrId.length() == 0) {
            corrId = UUID.randomUUID().toString();
            System.err.println("### " + tn() + " Initiate corrId to " + corrId);
        }

        MDC.put("corrId", corrId);
        MDC.put("component", componentName);
        System.err.println("### " + tn() + " MY FILTER SAVE CORR-ID AND COMPONENT:" + MDC.get("corrId") + "/" + MDC.get("component"));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            MDC.put("user", authentication.getName());
            System.err.println("### " + tn() + " MY FILTER SAVED USER:" + MDC.get("user"));
        }
        try {
            chain.doFilter(req, resp);
        } finally {
            System.err.println("### " + tn() + " MY FILTER SAVE CORR-ID AND COMPONENT:" + MDC.get("corrId") + "/" + MDC.get("component"));
            MDC.remove("corrId");
            MDC.remove("component");
            if (authentication != null) {
                MDC.remove("user");
            }
        }
    };

    private String tn() {
        return Thread.currentThread().getName();
    }

}
