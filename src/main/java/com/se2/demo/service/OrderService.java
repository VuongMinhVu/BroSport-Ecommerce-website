package com.se2.demo.service;

import com.se2.demo.dto.request.OrderRequest;
import com.se2.demo.dto.response.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    String preparePayment(OrderRequest request, HttpServletRequest servletRequest);
    OrderResponse processPaymentCallback(HttpServletRequest request);
}
