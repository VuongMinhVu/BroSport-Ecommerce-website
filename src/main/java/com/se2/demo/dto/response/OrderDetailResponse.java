package com.se2.demo.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class OrderDetailResponse {
    private String orderNumber;
    private String createdAt;
    private String orderStatus;
    private BigDecimal totalPrice;

    // Thông tin người nhận & vận chuyển
    private String recipientName;
    private String shippingAddress;
    private String shippingMethod;

    // Thông tin thanh toán
    private String paymentMethod;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal tax;
    private BigDecimal discount;

    // Danh sách sản phẩm
    private List<OrderItemResponse> items;
}
