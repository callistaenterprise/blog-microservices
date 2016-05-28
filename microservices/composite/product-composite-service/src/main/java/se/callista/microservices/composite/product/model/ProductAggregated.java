package se.callista.microservices.composite.product.model;

import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.recommendation.model.Recommendation;
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
    private List<RecommendationSummary> recommendations;
    private List<ReviewSummary> reviews;
    private List<Vendor> vendors;

    public ProductAggregated(Product product, List<Recommendation> recommendations, List<Review> reviews, List<Vendor> vendorList) {

        // 1. Setup product info
        this.productId = product.getProductId();
        this.name = product.getName();
        this.weight = product.getWeight();

        // 2. Copy summary recommendation info, if available
        if (recommendations != null)
            this.recommendations = recommendations.stream()
                .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
                .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        if (reviews != null)
            this.reviews = reviews.stream()
                .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                .collect(Collectors.toList());

        // 4. Assign summary vendors info, if available
        if (vendorList != null)
            this.vendors = vendorList;
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

    public List<RecommendationSummary> getRecommendations() {
        return recommendations;
    }

    public List<ReviewSummary> getReviews() {
        return reviews;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }
}
