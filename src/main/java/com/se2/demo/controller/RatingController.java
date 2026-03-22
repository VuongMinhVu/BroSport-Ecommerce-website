package com.se2.demo.controller;

import com.se2.demo.dto.request.RatingRequest;
import com.se2.demo.dto.response.ProductRatingSummaryResponse;
import com.se2.demo.dto.response.RatingResponse;
import com.se2.demo.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/api/v1/products/{productId}/ratings")
    public ResponseEntity<RatingResponse> createRating(
            @PathVariable Integer productId,
            @RequestParam Long userId,
            @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ratingService.createRating(productId, userId, request));
    }

    @GetMapping("/api/v1/products/{productId}/ratings")
    public ResponseEntity<ProductRatingSummaryResponse> getProductRatings(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ratingService.getProductRatings(productId, page, size));
    }

    @DeleteMapping("/api/v1/ratings/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long ratingId,
            @RequestParam Long userId) {
        ratingService.deleteRating(ratingId, userId);
        return ResponseEntity.noContent().build();
    }
}
