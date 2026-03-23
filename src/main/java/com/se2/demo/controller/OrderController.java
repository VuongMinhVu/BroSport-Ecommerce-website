package com.se2.demo.controller;

import com.se2.demo.dto.request.OrderRequest;
import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.OrderHistoryResponse;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest) {
        OrderResponse response = orderService.checkout(request, httpServletRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<OrderResponse> handleCallback(HttpServletRequest request) {
        OrderResponse response = orderService.processPaymentCallback(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@PathVariable Integer userId) {
        List<OrderHistoryResponse> history = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable String orderCode) {
        OrderDetailResponse detail = orderService.getOrderDetail(orderCode);
        return ResponseEntity.ok(detail);
    }
}