package se.callista.microservices.api.product.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import se.callista.microservices.util.ServiceUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.net.URI;
import java.security.Principal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ProductApiService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductApiService.class);

    @Inject
    ServiceUtils util;

    @Inject
    @Qualifier("restTemplateWithLogInterceptor")
    private RestOperations restTemplate;

    @Inject
    private LoadBalancerClient loadBalancer;

    @RequestMapping("/{productId}")
    @HystrixCommand(fallbackMethod = "defaultProductComposite")
    public ResponseEntity<String> getProductComposite(
        @PathVariable int productId,
        @RequestHeader(value="Authorization") String authorizationHeader,
        Principal currentUser) {

        MDC.put("productId", productId);
        LOG.info("ProductApi: User={}, Auth={}, called with productId={}", currentUser.getName(), authorizationHeader, productId);

        URI uri = loadBalancer.choose("productcomposite").getUri();
        String url = uri.toString() + "/product/" + productId;
        LOG.debug("GetProductComposite from URL: {}", url);

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        LOG.info("GetProductComposite http-status: {}", result.getStatusCode());
        LOG.debug("GetProductComposite body: {}", result.getBody());

        return util.createResponse(result);
    }

    /**
     * Fallback method for getProductComposite()
     *
     * @param productId
     * @return
     */
    public ResponseEntity<String> defaultProductComposite(
        @PathVariable int productId,
        @RequestHeader(value="Authorization") String authorizationHeader,
        Principal currentUser) {

        LOG.warn("Using fallback method for product-composite-service. User={}, Auth={}, called with productId={}", currentUser.getName(), authorizationHeader, productId);
        return new ResponseEntity<String>("", HttpStatus.BAD_GATEWAY);
    }
}
