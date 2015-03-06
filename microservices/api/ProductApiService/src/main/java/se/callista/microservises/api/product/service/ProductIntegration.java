package se.callista.microservises.api.product.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import se.callista.microservises.api.product.model.ProductAggregated;
import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.review.model.Review;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by magnus on 05/03/15.
 */
@Component
public class ProductIntegration {

    @Autowired
    private LoadBalancerClient loadBalancer;

    private String uri = "http://localhost:8081/stores";

    @HystrixCommand(fallbackMethod = "defaultProduct")
    public Product getProduct(int productId) {

        URI storesUri = URI.create(uri);

        ServiceInstance instance = null;
        try {
            instance = loadBalancer.choose("products");
            storesUri = URI.create(String.format("http://%s:%s", instance.getHost(),
                    instance.getPort()));
        } catch (RuntimeException e) {
            // Eureka not available
        }

        return new Product(productId, null, 0);
    }

    /**
     * Fallback method for getProduct()
     *
     * @param productId
     * @return
     */
    public Product defaultProduct(int productId) {
        return null;
    }

    @HystrixCommand(fallbackMethod = "defaultReviews")
    public List<Review> getReviews(int productId) {

        URI storesUri = URI.create(uri);

        ServiceInstance instance = null;
        try {
            instance = loadBalancer.choose("products");
            storesUri = URI.create(String.format("http://%s:%s", instance.getHost(),
                    instance.getPort()));
        } catch (RuntimeException e) {
            // Eureka not available
        }

        List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1"));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2"));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3"));

        return list;
    }

    /**
     * Fallback method for getReviews()
     *
     * @param productId
     * @return
     */
    public List<Review> defaultReviews(int productId) {
        return null;
    }
}