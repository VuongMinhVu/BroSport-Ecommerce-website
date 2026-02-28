package com.se2.demo.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailRequest {
    Integer colorId;
    Integer sizeId;
    BigDecimal price;
    BigDecimal compareAtPrice;
    Integer stockQuantity;
    Float weightGrams;
    String sku;
    List<ProductImageRequest> variantImages;
}
