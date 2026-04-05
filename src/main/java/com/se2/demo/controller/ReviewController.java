package com.se2.demo.controller;

import com.se2.demo.dto.request.ReviewRequest;
import com.se2.demo.dto.response.ReviewListResponse;
import com.se2.demo.dto.response.ReviewResponse;
import com.se2.demo.model.entity.User;
import com.se2.demo.service.ReviewService;
import com.se2.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request, Principal principal) {
        try {
            String email = principal.getName() != null ? principal.getName() : "admin@brosport.com";
            User user = userService.getUserByEmail(email);
            Integer currentUserId = user.getId();

            ReviewResponse response = reviewService.createReview(currentUserId, request);
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
