package com.se2.demo.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    Integer id;
    ColorResponse color;
    SizeResponse size;
    BigDecimal price;
    BigDecimal compareAtPrice;
    Integer stockQuantity;
    Float weightGrams;
    String sku;
    List<ProductImageResponse> variantImages;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
