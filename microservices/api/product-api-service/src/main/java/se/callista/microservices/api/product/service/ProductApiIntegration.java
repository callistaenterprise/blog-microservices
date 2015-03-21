package se.callista.microservices.api.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.review.model.Review;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by magnus on 05/03/15.
 */
@Component
public class ProductApiIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(ProductApiIntegration.class);

    @Autowired
    Util util;

    private RestTemplate restTemplate = new RestTemplate();

    // -------- //
    // PRODUCTS //
    // -------- //

    @HystrixCommand(fallbackMethod = "defaultProduct")
    public ResponseEntity<Product> getProduct(int productId) {

        URI uri = util.getServiceUrl("products", "http://localhost:8081/products");
        String url = uri.toString() + "/products/" + productId;
        LOG.debug("GetProduct from URL: {}", url);

        ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
        LOG.debug("GetProduct http-status: {}", resultStr.getStatusCode());
        LOG.debug("GetProduct body: {}", resultStr.getBody());

        Product product = response2Product(resultStr);
        LOG.debug("GetProduct.id: {}", product.getProductId());

        return util.createOkResponse(product);
    }

    /**
     * Fallback method for getProduct()
     *
     * @param productId
     * @return
     */
    public ResponseEntity<Product> defaultProduct(int productId) {
        LOG.warn("Using fallback method for product-service");
        return util.createResponse(null, HttpStatus.BAD_GATEWAY);
    }


    // ------- //
    // REVIEWS //
    // ------- //

    @HystrixCommand(fallbackMethod = "defaultReviews")
    public ResponseEntity<List<Review>> getReviews(int productId) {
        LOG.info("GetReviews...");

        URI uri = util.getServiceUrl("reviews", "http://localhost:8081/reviews");

        String url = uri.toString() + "/reviews?productId=" + productId;
        LOG.debug("GetReviews from URL: {}", url);

        ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
        LOG.debug("GetReviews http-status: {}", resultStr.getStatusCode());
        LOG.debug("GetReviews body: {}", resultStr.getBody());

        List<Review> reviews = response2Reviews(resultStr);
        LOG.debug("GetReviews.cnt {}", reviews.size());

        return util.createOkResponse(reviews);
    }


    /**
     * Fallback method for getReviews()
     *
     * @param productId
     * @return
     */
    public ResponseEntity<List<Review>> defaultReviews(int productId) {
        LOG.warn("Using fallback method for review-service");
        return util.createResponse(null, HttpStatus.BAD_GATEWAY);
    }

    // ----- //
    // UTILS //
    // ----- //


    private ObjectReader productReader = null;
    private ObjectReader getProductReader() {

        if (productReader != null) return productReader;

        ObjectMapper mapper = new ObjectMapper();
        return productReader = mapper.reader(Product.class);
    }

    private ObjectReader reviewsReader = null;
    private ObjectReader getReviewsReader() {
        if (reviewsReader != null) return reviewsReader;

        ObjectMapper mapper = new ObjectMapper();
        return reviewsReader = mapper.reader(new TypeReference<List<Review>>() {});
    }

    public Product response2Product(ResponseEntity<String> response) {
        try {
            return getProductReader().readValue(response.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Gereralize with <T> method, skip objectReader objects!
    private List<Review> response2Reviews(ResponseEntity<String> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List list = mapper.readValue(response.getBody(), new TypeReference<List<Review>>() {});
            List<Review> reviews = list;
            return reviews;

        } catch (IOException e) {
            LOG.warn("IO-err. Failed to read JSON", e);
            throw new RuntimeException(e);

        } catch (RuntimeException re) {
            LOG.warn("RTE-err. Failed to read JSON", re);
            throw re;
        }
    }


}