package com.se2.demo.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    Integer categoryId;
    Integer brandId;
    Integer genderId;
    Integer sportId;
    String name;
    String slug;
    String description;
    String feature;
    Map<String, Object> information;
    String status;
    BigDecimal originPrice;
    BigDecimal showPrice;
    List<ProductDetailRequest> productDetails;
    List<ProductImageRequest> productImages;
}
