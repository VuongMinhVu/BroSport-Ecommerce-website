package com.se2.demo.repository;

import com.se2.demo.model.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
    Optional<CartDetail> findByCartIdAndProductDetailId(Integer cartId, Integer productDetailId);
}