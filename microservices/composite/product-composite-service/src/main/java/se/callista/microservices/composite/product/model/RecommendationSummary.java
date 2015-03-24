package se.callista.microservices.composite.product.model;

/**
 * Created by magnus on 05/03/15.
 */
public class RecommendationSummary {

    private int recommendationId;
    private String author;
    private int rate;

    public RecommendationSummary(int recommendationId, String author, int rate) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRate() {
        return rate;
    }
}
