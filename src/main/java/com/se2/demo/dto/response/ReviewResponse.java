package com.se2.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private String fullName;
    private String avatarUrl;
    private Integer rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private List<ReviewResponse> replies;
}
