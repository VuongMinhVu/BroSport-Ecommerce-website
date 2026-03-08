package com.se2.demo.dto.async;

import com.se2.demo.model.entity.ProductDocument;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ProductEvent {
  ProductDocument productDocument;
  String eventType;
}
