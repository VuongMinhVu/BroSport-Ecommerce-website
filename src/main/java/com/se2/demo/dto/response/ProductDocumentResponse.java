package com.se2.demo.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocumentResponse {
  private Long id;
  private String name;
  private String slug;
  private Double showPrice;
  private Double originPrice;
  private String brandName;
  private String sportName;
  private String genderName;
  private String categoryName;
  private String thumbnail;
  private List<String> availableColors;
  private List<String> availableSizes;
}
