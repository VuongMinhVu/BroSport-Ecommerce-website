package com.se2.demo.controller;

import com.se2.demo.dto.request.OrderRequest;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody OrderRequest request, HttpServletRequest servletRequest) {
        String paymentUrl = orderService.preparePayment(request, servletRequest);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<OrderResponse> handleCallback(HttpServletRequest request) {
        OrderResponse response = orderService.processPaymentCallback(request);
        return ResponseEntity.ok(response);
    }
}