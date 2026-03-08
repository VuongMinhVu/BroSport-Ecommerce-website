package com.se2.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<CartDetailResponse> cartDetails;

    private LocalDateTime updatedAt;
}
