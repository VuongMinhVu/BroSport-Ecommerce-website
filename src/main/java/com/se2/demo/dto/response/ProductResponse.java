package com.se2.demo.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Integer id;
    CategoryResponse category;
    BrandResponse brand;
    List<TargetCustomerResponse> targetCustomers;
    String name;
    String slug;
    String description;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<ProductDetailResponse> productDetails;
    List<ProductImageResponse> productImages;
}
