package com.se2.demo.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.Map;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewListResponse {
    Page<ReviewResponse> reviews;
    Long totalReviews;
    Double averageRating;
    Map<Integer, Long> ratingCounts;
}
