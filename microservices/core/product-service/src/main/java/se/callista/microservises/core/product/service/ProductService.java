package se.callista.microservises.core.product.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservises.core.product.model.Product;

/**
 * Created by magnus on 04/03/15.
 */
@RestController
public class ProductService {

    @RequestMapping("/products/{productId}")
    public Product getProduct(
        @PathVariable int productId) {

        return new Product(productId, "name", 123);
    }
}
