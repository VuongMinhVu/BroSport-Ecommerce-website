package com.se2.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Integer id;              // ID đơn hàng trong DB
    private String orderNumber;      // Mã đơn hàng (VD: #BRS-123)
    private String paymentMethod;    // COD, VNPAY, BANK
    private String paymentUrl;       // Chỉ dùng khi chọn VNPAY
    private String estimatedDelivery;
    private String message;
    private String orderCode;

    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal total;
    private List<OrderItemResponse> items;
}