package com.se2.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailResponse {

    private Integer id;

    private Integer cartId;

    private Integer productDetailId;

    private LocalDateTime addedAt;

    private LocalDateTime updatedAt;
}