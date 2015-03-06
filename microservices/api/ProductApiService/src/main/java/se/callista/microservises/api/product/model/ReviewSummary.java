package se.callista.microservises.api.product.model;

/**
 * Created by magnus on 05/03/15.
 */
public class ReviewSummary {
    private int reviewId;
    private String author;
    private String subject;

    public ReviewSummary(int reviewId, String author, String subject) {
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }
}
