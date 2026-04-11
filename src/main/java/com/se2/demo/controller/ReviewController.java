package com.se2.demo.controller;

import com.se2.demo.dto.request.ReviewRequest;
import com.se2.demo.dto.response.ReviewListResponse;
import com.se2.demo.dto.response.ReviewResponse;
import com.se2.demo.service.ReviewService;
import com.se2.demo.service.UserService;
import com.se2.demo.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService; // THÊM DÒNG NÀY

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request, Principal principal) {
        try {
            // 1. Kiểm tra xem người dùng đã đăng nhập chưa
            if (principal == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập để đánh giá sản phẩm");
            }

            // 2. Lấy ID thật của User từ Session hiện tại
            User user = userService.getUserByEmail(principal.getName());

            // 3. Truyền ID thật vào hàm tạo Review
            ReviewResponse response = reviewService.createReview(user.getId(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ReviewListResponse> getReviewsByProduct(
            @PathVariable Integer productId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ReviewListResponse responses = reviewService.getReviewsByProduct(productId, rating, page, size);
        return ResponseEntity.ok(responses);
    }
}