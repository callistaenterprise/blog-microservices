package se.callista.microservises.core.review.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservices.util.SetProcTimeBean;
import se.callista.microservises.core.review.model.Review;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private SetProcTimeBean setProcTimeBean;

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
        LOG.info("/reviews called, processing time: {}", pt);

        sleep(pt);

        List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1"));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2"));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3"));

        LOG.info("/reviews response size: {}", list.size());

        return list;
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
