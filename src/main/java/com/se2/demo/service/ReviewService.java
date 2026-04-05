package com.se2.demo.service;

import com.se2.demo.dto.request.ReviewRequest;
import com.se2.demo.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;

import com.se2.demo.dto.response.ReviewListResponse;

public interface ReviewService {
    ReviewResponse createReview(Integer userId, ReviewRequest request);
    ReviewListResponse getReviewsByProduct(Integer productId, Integer rating, int page, int size);
}
