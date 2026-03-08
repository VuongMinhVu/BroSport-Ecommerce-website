package com.se2.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailRequest {

    // ID
    private Integer cartId;

    // ID của sản phẩm cụ thể
    private Integer productDetailId;
}