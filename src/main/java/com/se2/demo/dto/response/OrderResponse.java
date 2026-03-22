package com.se2.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    String orderNumber;      // #BRS-992834
    String estimatedDelivery; // Oct 24, 2023
    BigDecimal subtotal;
    BigDecimal shipping;
    BigDecimal total;
    List<OrderItemResponse> items;
}
