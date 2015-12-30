package se.callista.microservises.core.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservices.util.SetProcTimeBean;
import se.callista.microservises.core.product.model.Product;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private SetProcTimeBean setProcTimeBean;

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId
     * @return
     */
    @RequestMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {
        LOG.info("/product called");

        int pt = setProcTimeBean.calculateProcessingTime();
        LOG.info("/product called, processing time: {}", pt);

        sleep(pt);

        return new Product(productId, "name", 123);
    }

    private void sleep(int pt) {
        try {
            Thread.sleep(pt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sample usage:
     *
     *  curl "http://localhost:10002/set-processing-time?minMs=1000&maxMs=2000"
     *
     * @param minMs
     * @param maxMs
     */
    @RequestMapping("/set-processing-time")
    public void setProcessingTime(
            @RequestParam(value = "minMs", required = true) int minMs,
            @RequestParam(value = "maxMs", required = true) int maxMs) {

        LOG.info("/set-processing-time called: {} - {} ms", minMs, maxMs);

        setProcTimeBean.setDefaultProcessingTime(minMs, maxMs);
    }
}
