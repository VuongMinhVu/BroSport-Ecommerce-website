package com.se2.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    private Integer id;

    private Integer userId;

    private Integer productDetailId;

    private LocalDateTime updatedAt;
}
