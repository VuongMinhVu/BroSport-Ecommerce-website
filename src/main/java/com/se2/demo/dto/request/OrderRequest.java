package com.se2.demo.dto.request;

import lombok.Data;

@Data
public class OrderRequest {
    private String fullName;
    private String phone;
    private String shippingAddress;
    private String paymentMethod;
    private String voucherCode;

    // THÊM 3 TRƯỜNG NÀY ĐỂ HỖ TRỢ "MUA NGAY"
    private Boolean isBuyNow;
    private Integer productDetailId;
    private Integer quantity;
}