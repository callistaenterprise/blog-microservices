package se.callista.microservices.api.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservices.api.product.model.ProductAggregated;
import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.review.model.Review;

import java.util.Date;
import java.util.List;

/**
 * Created by magnus on 04/03/15.
 */
@RestController
@EnableHystrix
public class ProductApiService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductApiService.class);

    @Autowired
    ProductApiIntegration integration;

    @Autowired
    Util util;

    @RequestMapping("/")
    public String getProduct() {
        return "{\"timestamp\":\"" + new Date() + "\",\"content\":\"Hello from ProductAPi\"}";
    }

    @RequestMapping("/products/{productId}")
    public ResponseEntity<ProductAggregated> getProduct(@PathVariable int productId) {

        ResponseEntity<Product> productResult = integration.getProduct(productId);

        if (!productResult.getStatusCode().is2xxSuccessful()) {
            // We can't proceed, return whatever fault we got from the getProduct call
            return util.createResponse(null, productResult.getStatusCode());
        }

        ResponseEntity<List<Review>> reviewsResult = integration.getReviews(productId);
        List<Review> reviews = null;
        if (!reviewsResult.getStatusCode().is2xxSuccessful()) {
            // Something went wrong with getReviews, simply skip the review-information in the response
            LOG.warn("Call to getReviews failed: {}", reviewsResult.getStatusCode());
        } else {
            reviews = reviewsResult.getBody();
        }

        return util.createOkResponse(new ProductAggregated(productResult.getBody(), reviews));
    }
}
