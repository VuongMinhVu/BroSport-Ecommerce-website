package com.se2.demo.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageResponse {
    Integer id;
    String imageUrl;
    Boolean isMain;
    Integer sortOrder;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
