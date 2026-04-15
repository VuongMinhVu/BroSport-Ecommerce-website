package com.se2.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private Integer userId;
    private String fullName;
    private String phone;
    private String shippingAddress;
    private String paymentMethod;
    private String voucherCode;
    private BigDecimal shippingFee;

    // THÊM 3 TRƯỜNG NÀY ĐỂ HỖ TRỢ "MUA NGAY"
    private Boolean isBuyNow;
    private Integer productDetailId;
    private Integer quantity;
}