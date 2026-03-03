package com.se2.demo.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Integer id;
    String category;
    String brand;
    String gender;
    String sport;
    String name;
    String slug;
    String description;
    String feature;
    Map<String, Object> information;
    String status;
    BigDecimal price;
    BigDecimal compareAtPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<ProductDetailResponse> productDetails;
    List<ProductImageResponse> productImages;
}
