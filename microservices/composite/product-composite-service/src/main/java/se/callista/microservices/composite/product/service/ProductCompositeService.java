package se.callista.microservices.composite.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.microservices.composite.product.model.ProductAggregated;
import se.callista.microservices.model.Product;
import se.callista.microservices.model.Recommendation;
import se.callista.microservices.model.Review;
import se.callista.microservices.util.ServiceUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.CompletableFuture.allOf;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ProductCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeService.class);

    private final ProductCompositeIntegration integration;
    private final ServiceUtils util;

    @Inject
    public ProductCompositeService(ProductCompositeIntegration integration, ServiceUtils util) {
        this.integration = integration;
        this.util = util;
    }


    @RequestMapping("/")
    public String getProduct() {
        return "{\"timestamp\":\"" + new Date() + "\",\"content\":\"Hello from ProductAPi\"}";
    }

    @RequestMapping("/{productId}")
    public ResponseEntity<ProductAggregated> getProductAggregatedSync(@PathVariable int productId) {

        // 1. First get mandatory product information
        Product product = getBasicProductInfo(productId);

        // 2. Get optional recommendations
        List<Recommendation> recommendations = getRecommendations(productId);

        // 3. Get optional reviews
        List<Review> reviews = getReviews(productId);

        return util.createOkResponse(new ProductAggregated(product, recommendations, reviews, util.getServiceAddress()));
    }

    @GetMapping(path = "async/{productId}")
    public Mono<ProductAggregated> getProductAggregatedAsync(@PathVariable("productId") int id) {

        return Mono.zip(
            values -> new ProductAggregated(
                (Product)values[0],
                (List<Recommendation>)values[1],
                (List<Review>)values[2],
                util.getServiceAddress()),
            integration.getProductAsync(id),
            integration.getRecommendationsAsync(id).collectList(),
            integration.getReviewsAsync(id).collectList())

            .doOnSubscribe(s  -> LOG.debug("composite-async START, productId: {}", id))
            .doOnError    (ex -> LOG.warn ("composite-async ERROR", ex))
            .doOnSuccess  (p  -> LOG.debug("composite-async DONE"));
    }

    private Product getBasicProductInfo(@PathVariable int productId) {
        ResponseEntity<Product> productResult = integration.getProduct(productId);
        Product product = null;
        if (!productResult.getStatusCode().is2xxSuccessful()) {
            // Something went wrong with getProduct, simply skip the product-information in the response
            LOG.debug("Call to getProduct failed: {}", productResult.getStatusCode());
        } else {
            product = productResult.getBody();
        }
        return product;
    }

    private List<Review> getReviews(@PathVariable int productId) {
        ResponseEntity<List<Review>> reviewsResult = integration.getReviews(productId);
        List<Review> reviews = null;
        if (!reviewsResult.getStatusCode().is2xxSuccessful()) {
            // Something went wrong with getReviews, simply skip the review-information in the response
            LOG.debug("Call to getReviews failed: {}", reviewsResult.getStatusCode());
        } else {
            reviews = reviewsResult.getBody();
        }
        return reviews;
    }

    private List<Recommendation> getRecommendations(@PathVariable int productId) {
        List<Recommendation> recommendations = null;
        try {
            ResponseEntity<List<Recommendation>> recommendationResult = integration.getRecommendations(productId);
            if (!recommendationResult.getStatusCode().is2xxSuccessful()) {
                // Something went wrong with getRecommendations, simply skip the recommendation-information in the response
                LOG.debug("Call to getRecommendations failed: {}", recommendationResult.getStatusCode());
            } else {
                recommendations = recommendationResult.getBody();
            }
        } catch (Throwable t) {
            LOG.error("getProduct error", t);
            throw t;
        }
        return recommendations;
    }
}
