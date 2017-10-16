package se.callista.microservices.core.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.microservices.core.product.persistence.entity.ProductEntity;
import se.callista.microservices.core.product.persistence.repository.ProductRepository;
import se.callista.microservices.model.Product;
import se.callista.microservices.model.Recommendation;
import se.callista.microservices.util.CpuCruncherBean;
import se.callista.microservices.util.ServiceUtils;
import se.callista.microservices.util.SetProcTimeBean;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final SetProcTimeBean setProcTimeBean;
    private final CpuCruncherBean cpuCruncher;
    private final ServiceUtils util;
    private final ProductRepository repository;


    @Inject
    public ProductService(SetProcTimeBean setProcTimeBean, CpuCruncherBean cpuCruncher, ServiceUtils util, ProductRepository repository) {
        this.setProcTimeBean = setProcTimeBean;
        this.cpuCruncher = cpuCruncher;
        this.util = util;
        this.repository = repository;
    }

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId
     * @return
     */
    @RequestMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {

        int pt = setProcTimeBean.calculateProcessingTime();
        LOG.info("/product/{} called, processing time: {}", productId, pt);

        sleep(pt);

        cpuCruncher.exec();

        return repository.findByProductId(productId)
            .map(e -> toApi(e))
            .doOnNext(p -> LOG.debug("/product returns the product: {}", p))
            .block();
    }

    @GetMapping(path = "/product-async/{productId}")
    public Mono<Product> getProductAsync(@PathVariable(value = "productId") int id) {

        LOG.trace("### Called: /product-async/{}", id);

        return repository.findByProductId(id)
            .map(e -> toApi(e))
            .doOnSubscribe(s  -> LOG.debug("product-async START, productId: {}", id))
            .doOnError    (ex -> LOG.warn ("product-async ERROR", ex))
            .doOnSuccess  (p  -> LOG.debug("product-async DONE, product: {}", p));
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

    private Product toApi(ProductEntity e) {
        return new Product(e.getProductId(), e.getName(), e.getWeight(), util.getServiceAddress());
    }

    private void sleep(int pt) {
        try {
            Thread.sleep(pt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
