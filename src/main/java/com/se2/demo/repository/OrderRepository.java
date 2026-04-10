package com.se2.demo.repository;

import com.se2.demo.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Cho phép đánh giá nếu trạng thái là Đã Giao (DELIVERED) hoặc Đã Thanh
    // Toán (PAID)
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi JOIN oi.productDetail pd WHERE o.user.id = :userId AND pd.product.id = :productId AND (o.orderStatus = 'DELIVERED' OR o.paymentStatus = 'PAID')")
    boolean hasUserPurchasedProduct(@org.springframework.data.repository.query.Param("userId") Integer userId,
            @org.springframework.data.repository.query.Param("productId") Integer productId);
}