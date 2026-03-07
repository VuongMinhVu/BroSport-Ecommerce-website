package com.se2.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {

    private Integer userId;

    // ID chi tiết sản phẩm được thêm vào (theo sơ đồ ERD)
    // Lưu ý: Tên biến khớp với logic xử lý trong Service
    private Integer productDetailId;
}