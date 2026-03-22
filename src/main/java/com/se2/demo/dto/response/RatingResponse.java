package com.se2.demo.dto.response;

import java.time.LocalDateTime;

public record RatingResponse(
        Long id,
        Long userId,
        Integer ratingStar,
        String content,
        LocalDateTime createdAt
) {
}
