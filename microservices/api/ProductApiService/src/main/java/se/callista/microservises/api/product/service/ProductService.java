package se.callista.microservises.api.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservises.api.product.model.ProductAggregated;
import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.review.model.Review;

import java.util.List;

/**
 * Created by magnus on 04/03/15.
 */
@RestController
public class ProductService {

    @Autowired
    ProductIntegration integration;

    @RequestMapping("/products/{productId}")
    public ProductAggregated getProduct(@PathVariable int productId) {

        Product product = integration.getProduct(productId);
        List<Review> reviews = integration.getReviews(productId);

        return new ProductAggregated(product, reviews);
    }
}
