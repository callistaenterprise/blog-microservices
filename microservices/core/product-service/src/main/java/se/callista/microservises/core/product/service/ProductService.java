package se.callista.microservises.core.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservises.core.product.model.Product;

/**
 * Created by magnus on 04/03/15.
 */
@RestController
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId
     * @return
     */
    @RequestMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {
        LOG.info("/product called");

        return new Product(productId, "name", 123);
    }
}
