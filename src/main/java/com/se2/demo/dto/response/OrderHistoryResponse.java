package com.se2.demo.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class OrderHistoryResponse {
    private Integer orderId;
    private String orderNumber;
    private Integer itemCount;
    private String date;
    private BigDecimal total;
    private String status;        // order staus
}