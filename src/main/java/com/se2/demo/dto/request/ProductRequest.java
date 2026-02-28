package com.se2.demo.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    Integer categoryId;
    Integer brandId;
    List<Integer> targetCustomerIds;
    String name;
    String slug;
    String description;
    String status;
    List<ProductDetailRequest> productDetails;
    List<ProductImageRequest> productImages;
}
