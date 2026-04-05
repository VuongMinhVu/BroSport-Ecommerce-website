package com.se2.demo.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer productId;
    private Integer rating;
    private String title;
    private String comment;
    private Integer parentReviewId;
}
