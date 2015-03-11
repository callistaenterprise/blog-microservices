package se.callista.microservises.core.review.service;

import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.callista.microservises.core.review.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magnus on 04/03/15.
 */
@RestController
@EnableHystrix
public class ReviewService {

    /**
     * Sample usage: curl localhost:10021/products/1
     * 
     * @param productId
     * @return
     */
    @RequestMapping("/reviews")
    public List<Review> getReviews(
        @RequestParam(value = "productId",  required = true) int productId) {

        List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1"));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2"));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3"));

        return list;
    }
}
