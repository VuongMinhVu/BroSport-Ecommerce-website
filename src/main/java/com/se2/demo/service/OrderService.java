package com.se2.demo.service;

import com.se2.demo.dto.request.OrderRequest;
import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.OrderHistoryResponse;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {

    OrderResponse checkout(User user, OrderRequest request, HttpServletRequest httpServletRequest);
    OrderDetailResponse getOrderDetail(String orderCode);
    OrderResponse processPaymentCallback(HttpServletRequest request);
    List<OrderHistoryResponse> getOrderHistory(Integer userId);
}