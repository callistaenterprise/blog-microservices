package se.callista.microservises.api.product.model;

import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.review.model.Review;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by magnus on 04/03/15.
 */
public class ProductAggregated {
    private int productId;
    private String name;
    private int weight;
    private List<ReviewSummary> reviews;

    public ProductAggregated(Product product, List<Review> reviews) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.weight = product.getWeight();

        if (reviews != null)
            this.reviews = reviews.stream()
                .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                .collect(Collectors.toList());
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<ReviewSummary> getReviews() {
        return reviews;
    }
}
