package com.se2.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RatingRequest(
        @NotNull(message = "ratingStar must not be null")
        @Min(value = 1, message = "ratingStar must be at least 1")
        @Max(value = 5, message = "ratingStar must not exceed 5")
        Integer ratingStar,

        String content
) {
}
