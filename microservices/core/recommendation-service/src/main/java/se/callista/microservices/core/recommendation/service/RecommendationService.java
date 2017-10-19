package se.callista.microservices.core.recommendation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.microservices.core.recommendation.persistence.entity.RecommendationEntity;
import se.callista.microservices.core.recommendation.persistence.repository.RecommendationRepository;
import se.callista.microservices.model.Recommendation;
import se.callista.microservices.model.Review;
import se.callista.microservices.util.CpuCruncherBean;
import se.callista.microservices.util.ServiceUtils;
import se.callista.microservices.util.SetProcTimeBean;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.ArrayList;
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
public class RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);

    private final SetProcTimeBean setProcTimeBean;
    private final CpuCruncherBean cpuCruncher;
    private final ServiceUtils util;
    private final RecommendationRepository repository;

    @Inject
    public RecommendationService(SetProcTimeBean setProcTimeBean, CpuCruncherBean cpuCruncher, ServiceUtils util, RecommendationRepository repository) {
        this.setProcTimeBean = setProcTimeBean;
        this.cpuCruncher = cpuCruncher;
        this.util = util;
        this.repository = repository;
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
     * Sample usage: curl $HOST:$PORT/recommendation?productId=1
     *
     * @param productId
     * @return
     */
    @RequestMapping("/recommendation")
    public List<Recommendation> getRecommendations(
        @RequestParam(value = "productId",  required = true) int productId) {

        try {
            int pt = setProcTimeBean.calculateProcessingTime();
            LOG.info("/recommendation?productId={} called, processing time: {}", productId, pt);

            sleep(pt);

            cpuCruncher.exec();

            List<Recommendation> list = repository.findByProductId(productId).map(e -> toApi(e)).collectList().block();

            LOG.debug("/recommendation response size: {}", list.size());

            return list;

        } catch (RuntimeException ex) {
            LOG.warn("recommendation ERROR", ex);
            throw ex;
        }
    }

    @GetMapping(path = "/recommendation-async")
    public Flux<Recommendation> getRecommendationsAsync(
        @RequestParam(value = "productId",  required = true) int id) {

        LOG.trace("### Called: /recommendation-async/?productId={}", id);

        return repository.findByProductId(id)
            .map(e -> toApi(e))
            .doOnSubscribe(s  -> LOG.debug("recommendation-async START, productId: {}", id))
            .doOnError    (ex -> LOG.warn("recommendation-async ERROR", ex))
            .doOnComplete (() -> LOG.debug("recommendation-async DONE"));
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

    private Recommendation toApi(RecommendationEntity e) {
        return new Recommendation(e.getProductId(), e.getRecommendationId(), e.getAuthor(), e.getRate(), e.getContent(), util.getServiceAddress());
    }

    private void sleep(int pt) {
        try {
            Thread.sleep(pt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
