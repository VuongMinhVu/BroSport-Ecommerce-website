package com.se2.demo.service;

import com.se2.demo.dto.request.RatingRequest;
import com.se2.demo.dto.response.ProductRatingSummaryResponse;
import com.se2.demo.dto.response.RatingResponse;

public interface RatingService {
    RatingResponse createRating(Integer productId, Long userId, RatingRequest request);
    ProductRatingSummaryResponse getProductRatings(Integer productId, int page, int size);
    void deleteRating(Long ratingId, Long userId);
}
