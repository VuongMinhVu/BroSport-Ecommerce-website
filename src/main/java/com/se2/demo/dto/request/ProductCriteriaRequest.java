package com.se2.demo.dto.request;

import java.util.List;

public record ProductCriteriaRequest(
   String keyword,
   Integer page,
   Integer size,
   String brands,
   String categories,
   String gender,
   String sports,

   Double minPrice,
   Double maxPrice,
   String sortBy,
   String sortDir
) {

}
