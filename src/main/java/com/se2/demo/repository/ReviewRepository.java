package com.se2.demo.repository;

import com.se2.demo.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.parentReview IS NULL AND (:rating IS NULL OR r.rating = :rating) ORDER BY r.createdAt ASC")
    Page<Review> findByProductIdAndRatingAndParentReviewIsNull(@Param("productId") Integer productId, @Param("rating") Integer rating, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.parentReview IS NULL")
    Double calculateAverageRating(@Param("productId") Integer productId);

    boolean existsByUserIdAndProductIdAndParentReviewIsNull(Integer userId, Integer productId);
}
