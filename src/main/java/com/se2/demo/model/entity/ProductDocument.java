package com.se2.demo.model.entity;

import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Document(indexName = "product")
@Setting(settingPath = "esconfig/elastic-analyzer.json")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDocument {
  @Id
  Long id;

  @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "autocomplete_search")
  String name;

  @Field(type = FieldType.Keyword)
  String slug;

  @Field(type = FieldType.Double)
  Double showPrice;

  Double originPrice;

  @Field(type = FieldType.Keyword)
  String brandName;

  @Field(type = FieldType.Keyword)
  String sportName;

  @Field(type = FieldType.Keyword)
  String genderName;

  @Field(type = FieldType.Keyword)
  String categoryName;

  @Field(type = FieldType.Text, index = false)  // ảnh
  String thumbnail;

  @Field(type = FieldType.Keyword)
  List<String> availableColors;

  @Field(type = FieldType.Keyword)
  List<String> availableSizes;

}
