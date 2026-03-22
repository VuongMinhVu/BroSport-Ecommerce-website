package com.se2.demo.dto.response;

public record ProductRatingSummaryResponse(
        Double avgRating,
        Long totalReviews,
        PageResponse<RatingResponse> ratings
) {
}
