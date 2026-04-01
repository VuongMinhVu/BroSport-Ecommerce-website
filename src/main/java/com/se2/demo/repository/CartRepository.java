package com.se2.demo.repository;

import com.se2.demo.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    java.util.List<Cart> findByUserIdAndProductDetailId(Integer userId, Integer productDetailId);
}