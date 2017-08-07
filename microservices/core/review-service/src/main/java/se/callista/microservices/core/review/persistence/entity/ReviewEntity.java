package se.callista.microservices.core.review.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Created by magnus on 04/03/15.
 */
@Entity
@IdClass(ReviewEntityPK.class)
@Table(name = "review")
public class ReviewEntity {

    @Id
    private int productId;

    @Id
    private int reviewId;

    private String author;
    private String subject;
    private String content;

    public ReviewEntity() {
    }

    public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format(
            "ReviewEntity[%d, %d, '%s', '%s', '%s']",
            productId, reviewId, author, subject, content);
    }
}
