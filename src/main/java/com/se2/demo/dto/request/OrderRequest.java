package com.se2.demo.dto.request;

import lombok.Data;

@Data
public class OrderRequest {
    private Integer userId;
    private String fullName;
    private String phone;
    private String shippingAddress;
    private String paymentMethod;
    private String voucherCode;
}