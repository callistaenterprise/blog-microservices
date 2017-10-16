package se.callista.microservices.core.review.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import se.callista.microservices.core.review.persistence.entity.ReviewEntity;
import se.callista.microservices.core.review.persistence.repository.ReviewRepository;
import se.callista.microservices.model.Review;
import se.callista.microservices.util.CpuCruncherBean;
import se.callista.microservices.util.ServiceUtils;
import se.callista.microservices.util.SetProcTimeBean;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    private final SetProcTimeBean setProcTimeBean;
    private final CpuCruncherBean cpuCruncher;
    private final ServiceUtils util;
    private final String mySecretProperty;
    private final ReviewRepository repository;
    private final Scheduler scheduler;

    @Inject
    public ReviewService(
        @Value("${my-secret-property:UNKNOWN}") String mySecretProperty,
        SetProcTimeBean setProcTimeBean, CpuCruncherBean cpuCruncher, ServiceUtils util, ReviewRepository repository, Scheduler scheduler) {

        this.mySecretProperty = mySecretProperty;
        this.setProcTimeBean = setProcTimeBean;
        this.cpuCruncher = cpuCruncher;
        this.util = util;
        this.repository = repository;
        this.scheduler = scheduler;
    }

    /*
    private int port;

    @Value("local.server.port")
    public void setPort (int port) {
        LOG.info("getReviews will be called on port: {}", port);
        this.port = port;
    }
    */

    /**
     * Sample usage: curl $HOST:$PORT/review?productId=1
     *
     * @param productId
     * @return
     */
    @RequestMapping("/review")
    public List<Review> getReviews(
            @RequestParam(value = "productId",  required = true) int productId) {

        int pt = setProcTimeBean.calculateProcessingTime();
        LOG.info("/reviews?productId={} called, processing time: {}",productId, pt);

        LOG.info("mySecretProperty: {}", mySecretProperty);

        sleep(pt);

        cpuCruncher.exec();

        List<Review> list = repository.findByProductId(productId).stream().map(e -> toApi(e)).collect(Collectors.toList());

        LOG.debug("/reviews response size: {}", list.size());

        return list;
    }

    @GetMapping(path = "/review-async")
    public Flux<Review> getReviewsAsync(
        @RequestParam(value = "productId",  required = true) int id) {

        LOG.trace("### Called: /review-async/?productId={}", id);

        return asyncFlux(repository.findByProductId(id))
            .map(e -> toApi(e))
            .doOnSubscribe(s  -> LOG.debug("review-async START, productId: {}", id))
            .doOnError    (ex -> LOG.warn ("review-async ERROR", ex))
            .doOnComplete (() -> LOG.debug("review-async DONE"));
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

    private Review toApi(ReviewEntity e) {
        return new Review(e.getProductId(), e.getReviewId(), e.getAuthor(), e.getSubject(), e.getContent(), util.getServiceAddress());
    }

    private void sleep(int pt) {
        try {
            Thread.sleep(pt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private <T> Mono<T> asyncMono(Callable<T> callable) {
        return Mono.fromCallable(callable).publishOn(scheduler);
    }

    private <T> Flux<T> asyncFlux(Iterable<T> iterable) {
        return Flux.fromIterable(iterable).publishOn(scheduler);
    }

    private <T> Flux<T> asyncFlux(Stream<T> stream) {
        return Flux.fromStream(stream).publishOn(scheduler);
    }

    private <T> Mono<ResponseEntity<T>> asyncMonoResponse(Callable<T> callable, HttpStatus status) {
        return asyncMono(() -> new ResponseEntity<T>(callable.call(), status));
    }
}
