package com.se2.demo.repository;

import com.se2.demo.model.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Page<Rating> findAllByProductId(Integer productId, Pageable pageable);

    @Query("SELECT AVG(r.ratingStar) FROM Rating r WHERE r.product.id = :productId")
    Double findAvgRatingByProductId(@Param("productId") Integer productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Integer productId);
}
