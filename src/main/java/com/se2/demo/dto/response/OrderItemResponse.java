package com.se2.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    String productName;
    String variantInfo; // Size: L | Color: Midnight Black
    Integer quantity;
    BigDecimal price;
    String imageUrl;
}