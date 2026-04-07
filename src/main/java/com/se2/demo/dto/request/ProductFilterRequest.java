package com.se2.demo.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequest {
    String keyword;
    Integer categoryId;
    Integer brandId;
    Integer genderId;
    Integer sportId;
    String categories;
    String brands;
    String gender;
    String sports;
    Integer colorId;
    Integer sizeId;
    BigDecimal minPrice;
    BigDecimal maxPrice;

    // Pagination parameters
    @Builder.Default
    Integer page = 0;

    @Builder.Default
    Integer size = 12;

    String sortBy;
    String sortDir;
}
