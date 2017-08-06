package se.callista.microservices.core.review.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import se.callista.microservices.core.review.persistence.entity.ReviewEntity;
import se.callista.microservices.core.review.persistence.entity.ReviewEntityPK;

import java.util.Collection;

public interface ReviewRepository extends CrudRepository<ReviewEntity, ReviewEntityPK> {

    Collection<ReviewEntity> findByProductId(int productId);
}
